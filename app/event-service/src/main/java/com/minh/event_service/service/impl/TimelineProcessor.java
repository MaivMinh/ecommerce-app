package com.minh.event_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.common.enums.GameEventType;
import com.minh.common.utils.AppUtils;
import com.minh.event_service.DTO.UserScoreData;
import com.minh.event_service.entity.PlayerVoucher;
import com.minh.event_service.entity.TimelineEvent;
import com.minh.event_service.payload.response.PlayerVoucherResponse;
import com.minh.event_service.payload.response.VoucherResponse;
import com.minh.event_service.service.GameLogicHandler;
import com.minh.event_service.service.PlayerVoucherService;
import com.minh.event_service.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimelineProcessor {
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final GameLogicHandler gameLogicHandler;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final VoucherService voucherService;
    private final PlayerVoucherService playerVoucherService;

    @Transactional
    public void process(String raw) throws Exception {
        TimelineEvent event = objectMapper.readValue(raw, TimelineEvent.class);
        log.info("Processing timeline event {}", raw);

        if (event.getType().equals(GameEventType.CLEANUP.toString())) {
            /// Xóa toàn bộ dữ liệu liên quan đến Event trong Redis.
            String scoreKey = "event:" + event.getEventId() + ":scores";
            String playerVoucherKey = "event:" + event.getEventId() + ":playerVouchers";
            String participantsKey = "event:current:participants";
            redisTemplate.delete(scoreKey);
            redisTemplate.delete(playerVoucherKey);
            redisTemplate.delete(participantsKey);
            redisTemplate.delete("event:" + event.getEventId() + ":ranking:snapshot");
            deleteByPattern("event:" + event.getEventId() + ":username:*");
            deleteByPattern("event:attendance:" + event.getEventId() + ":user:*");
            redisTemplate.delete("event:" + event.getEventId() + ":ranking");
            log.info("Cleaned up Redis data for event {}", event.getEventId());

            /// Xóa Observer.
            gameLogicHandler.cleanupMilestoneData(event.getEventId());
            return;
        }

        Map<String, Object> kafkaEvent = new HashMap<>();
        kafkaEvent.put("type", event.getType());
        kafkaEvent.put("eventId", event.getEventId());
        kafkaEvent.put("executeAt", System.currentTimeMillis());
        kafkaEvent.put("payload", event.getPayload());

        if (event.getType().equals(GameEventType.GAME_RESULT.toString())) {
            log.info("Preparing game result for event {}", event.getEventId());
            String snapshotKey = "event:" + event.getEventId() + ":ranking:snapshot";

            List<Object> rawData = redisTemplate.opsForList().range(snapshotKey, 0, -1);
            if (!CollectionUtils.isEmpty(rawData)) {
                List<UserScoreData> result = rawData.stream()
                        .filter(o -> o instanceof UserScoreData)
                        .map(o -> (UserScoreData) o)
                        .toList();
                kafkaEvent.put("payload", result);
            }

            String playerVoucherKey = "event:" + event.getEventId() + ":playerVouchers";
            List<Object> pvResult = redisTemplate.opsForList().range(playerVoucherKey, 0, -1);
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
            String rankingKey = "event:" + event.getEventId() + ":ranking";
            Set<ZSetOperations.TypedTuple<Object>> top =
                    redisTemplate.opsForZSet()
                            .reverseRangeWithScores(rankingKey, 0, -1);
            List<UserScoreData> ranking = new ArrayList<>();
            if (!CollectionUtils.isEmpty(top)) {
                for (ZSetOperations.TypedTuple<Object> tuple : top) {
                    String username = (String) tuple.getValue();
                    int score = tuple.getScore().intValue();
                    String playerKey = "event:" + event.getEventId() + ":username:" + username;
                    /*
                     * event:1:username:maivanminh   -> Hash atomic. Mỗi người chơi sẽ có riêng một key và lưu trữ các field:
                     *   - score: điểm số hiện tại.
                     *   - correct: số câu trả lời đúng.
                     * */
                    Map<Object, Object> fields = redisTemplate.opsForHash().entries(playerKey);
                    int correct = Integer.parseInt(
                            fields.getOrDefault("correct", 0).toString()
                    );
                    ranking.add(
                            UserScoreData.builder()
                                    .username(username)
                                    .score(score)
                                    .correct(correct)
                                    .build()
                    );
                }
            }

            /// Lưu lại vào Redis để phục vụ việc trả về kết quả sau này.
            String snapshotKey = "event:" + event.getEventId() + ":ranking:snapshot";
            redisTemplate.delete(snapshotKey);
            /// Cần phải chuyển về mảng. Nếu không, lát nữa lấy lên thành List<Object> thì Object = List<UserScoreData>.
            if (!CollectionUtils.isEmpty(ranking)) {
                redisTemplate.opsForList().rightPushAll(snapshotKey, ranking.toArray());
            }


            /// Liên kết các voucher cho người thắng cuộc.
            List<PlayerVoucherResponse> playerVouchers = new ArrayList<>();
            List<PlayerVoucher> data = new ArrayList<>();

            List<VoucherResponse> vouchers = voucherService.getVouchersByCampaignId(event.getEventId());
            List<VoucherResponse> sortedVouchers = vouchers.stream()
                    .sorted(Comparator.comparingInt(VoucherResponse::getVoucherOrder))
                    .toList();
            for (int i = 0; i < Math.min(vouchers.size(), ranking.size()); i++) {
                UserScoreData usd = ranking.get(i);
                VoucherResponse vr = sortedVouchers.get(i);
                data.add(
                        PlayerVoucher.builder()
                                .id(AppUtils.generateUUIDv7())
                                .voucherId(vr.getId())
                                .campaignId(vr.getCampaignId())
                                .code(vr.getCode())
                                .redeemedAt(Instant.now())
                                .used(Boolean.FALSE)
                                .username(usd.getUsername())
                                .discountPercentage(vr.getDiscountPercentage())
                                .value(vr.getValue())
                                .maxValue(vr.getMaxValue())
                                .build()
                );
            }
            /// Lưu vào DB.
            if (!data.isEmpty()) {
                playerVouchers = playerVoucherService.assignVoucherToUserBatch(data);
            }

            String playerVoucherKey = "event:" + event.getEventId() + ":playerVouchers";
            redisTemplate.delete(playerVoucherKey);
            if (!CollectionUtils.isEmpty(playerVouchers)) {
                redisTemplate.opsForList().rightPushAll(playerVoucherKey, playerVouchers.toArray());
            }
        }
    }

    public void deleteByPattern(String pattern) {
        ScanOptions options = ScanOptions.scanOptions()
                .match(pattern)
                .count(1000)
                .build();

        Cursor<byte[]> cursor = redisTemplate
                .getConnectionFactory()
                .getConnection()
                .scan(options);

        List<byte[]> keys = new ArrayList<>();
        cursor.forEachRemaining(keys::add);

        if (!keys.isEmpty()) {
            redisTemplate.delete(
                    keys.stream()
                            .map(String::new)
                            .toList()
            );
        }
    }
}
