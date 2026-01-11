package com.minh.event_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.event_service.DTO.UserScoreData;
import com.minh.event_service.entity.TimelineEvent;
import com.minh.event_service.entity.Voucher;
import com.minh.event_service.enums.GameEventType;
import com.minh.event_service.payload.response.PlayerVoucherResponse;
import com.minh.event_service.payload.response.VoucherResponse;
import com.minh.event_service.repository.VoucherRepository;
import com.minh.event_service.service.PlayerVoucherService;
import com.minh.event_service.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.time.Duration;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimelineWorker {
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TIMELINE_KEY = "event:timeline";
    private static final String LOCK_PREFIX = "event:lock:";
    private static final int BATCH_SIZE = 20;
    private final VoucherRepository voucherRepository;
    private final VoucherService voucherService;
    private final PlayerVoucherService playerVoucherService;

    @Scheduled(fixedDelay = 1000)
    public void poll() {
        long now = System.currentTimeMillis();

        Set<Object> events = redisTemplate.opsForZSet()
                .rangeByScore(TIMELINE_KEY, 0, now, 0, BATCH_SIZE);
        log.info("Found {} timeline events to process", events == null ? 0 : events.size());

        if (events == null || events.isEmpty()) return;

        for (Object raw : events) {
            String rawString = (String) raw;
            if (tryLock(rawString)) {   ///  Redis lock.
                try {
                    process(rawString);
                    redisTemplate.opsForZSet().remove(TIMELINE_KEY, raw);
                } catch (Exception e) {
                    log.error("Process timeline event failed", e);
                } finally {
                    unlock(rawString);
                }
            }
        }
    }

    private void process(String raw) throws Exception {
        TimelineEvent event = objectMapper.readValue(raw, TimelineEvent.class);
        log.info("Processing timeline event {}", raw);

        if (event.getType().equals(GameEventType.CLEANUP.toString())) {
            /// Xóa toàn bộ dữ liệu liên quan đến Event trong Redis.
            String scoreKey = "event:" + event.getEventId() + ":scores";
            String rankingKey = "event:" + event.getEventId() + ":ranking";
            String playerVoucherKey = "event:" + event.getEventId() + ":playerVouchers";

            redisTemplate.delete(scoreKey);
            redisTemplate.delete(rankingKey);
            redisTemplate.delete(playerVoucherKey);

            log.info("Cleaned up Redis data for event {}", event.getEventId());
            return;
        }

        Map<String, Object> kafkaEvent = new HashMap<>();
        kafkaEvent.put("type", event.getType());
        kafkaEvent.put("eventId", event.getEventId());
        kafkaEvent.put("executeAt", System.currentTimeMillis());
        kafkaEvent.put("payload", event.getPayload());

        if (event.getType().equals(GameEventType.GAME_RESULT.toString())) {
            log.info("Preparing game result for event {}", event.getEventId());
            String rankingKey = "event:" + event.getEventId() + ":ranking";
            Set<Object> result = redisTemplate.opsForSet().members(rankingKey);
            List<UserScoreData> data = new ArrayList<>();
            if (!CollectionUtils.isEmpty(result)) {
                data = result.stream()
                        .map(r -> {
                            if (r instanceof UserScoreData usd) {
                                return usd;
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .toList();
            }
            kafkaEvent.put("payload", data);

            String playerVoucherKey = "event:" + event.getEventId() + ":playerVouchers";
            Set<Object> pvResult = redisTemplate.opsForSet().members(playerVoucherKey);
            List<PlayerVoucherResponse> pvData = new ArrayList<>();
            if (!CollectionUtils.isEmpty(pvResult)) {
                pvData = pvResult.stream()
                        .map(r -> {
                            if (r instanceof PlayerVoucherResponse pvr) {
                                return pvr;
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .toList();
            }
            kafkaEvent.put("vouchers", pvData);
            log.info("ranking size {}, voucher size {}",
                    data.size(),
                    pvData.size()
            );
        }

        kafkaTemplate.send(
                "event.realtime",
                event.getEventId(),
                objectMapper.writeValueAsString(kafkaEvent)
        );

        if (event.getType().equals(GameEventType.SCORING.toString())) {
            /// Thực hiện tính toán kết quả.
            log.info("Calculating scores for event {}", event.getEventId());
            /// Lấy toàn bộ dữ liệu trong Cache.
            String userScoreKey = "event:" + event.getEventId() + ":scores";
            List<Object> values = redisTemplate.opsForHash().values(userScoreKey);
            if (!CollectionUtils.isEmpty(values)) {
                List<UserScoreData> scoreDataList = values.stream()
                        .map(v -> {
                            if (v instanceof UserScoreData data) {
                                return data;
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)   /// Chỉ lấy ra các object có kiểu UserScoreData.
                        .toList();


                /// Sắp xếp lại danh sách theo số điểm.
                List<UserScoreData> sortedList = scoreDataList.stream()
                        .sorted((a, b) -> Integer.compare(b.getScore(), a.getScore()))
                        .toList();

                /// Lưu vào Redis.
                String rankingKey = "event:" + event.getEventId() + ":ranking";
                redisTemplate.delete(rankingKey);
                sortedList.forEach(data -> redisTemplate.opsForSet().add(rankingKey, data));

                /// Liên kết các voucher cho người thắng cuộc.
                List<PlayerVoucherResponse> playerVouchers = new ArrayList<>();

                List<VoucherResponse> vouchers = voucherService.getVouchersByCampaignId(event.getEventId());
                List<VoucherResponse> sortedVouchers = vouchers.stream()
                        .sorted(Comparator.comparing(VoucherResponse::getVoucherOrder).reversed())
                        .toList();
                for (int i = 0; i < Math.min(vouchers.size(), sortedList.size()); i++) {
                    UserScoreData usd = sortedList.get(i);
                    VoucherResponse vr = sortedVouchers.get(i);
                    playerVouchers.add(playerVoucherService.assignVoucherToUser(
                            usd, vr
                    ));
                }

                String playerVoucherKey = "event:" + event.getEventId() + ":playerVouchers";
                redisTemplate.delete(playerVoucherKey);
                playerVouchers.forEach(data -> redisTemplate.opsForSet().add(playerVoucherKey, data));
            } else {
                log.info("No score data found for event {}", event.getEventId());
                String rankingKey = "event:" + event.getEventId() + ":ranking";
                redisTemplate.delete(rankingKey);
                redisTemplate.opsForSet().add(rankingKey, new ArrayList<>());
            }
        }
    }

    private boolean tryLock(String raw) {
        String key = LOCK_PREFIX + DigestUtils.md5DigestAsHex(raw.getBytes());
        Boolean ok = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", Duration.ofSeconds(5));
        return Boolean.TRUE.equals(ok);
    }

    private void unlock(String raw) {
        String key = LOCK_PREFIX + DigestUtils.md5DigestAsHex(raw.getBytes());
        redisTemplate.delete(key);
    }
}
