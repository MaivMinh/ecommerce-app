package com.minh.event_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.event_service.entity.TimelineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    @Scheduled(fixedDelay = 1000)
    public void poll() {
        long now = System.currentTimeMillis();

        Set<Object> events = redisTemplate.opsForZSet()
                .rangeByScore(TIMELINE_KEY, 0, now, 0, BATCH_SIZE);
        log.info("Found {} timeline events to process", events == null ? 0 : events.size());

        if (events == null || events.isEmpty()) return;

        for (Object raw : events) {
            String rawString = (String) raw;
            if (tryLock(rawString)) {
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
        log.info("Processing timeline event {}",raw);

        Map<String, Object> kafkaEvent = new HashMap<>();
        kafkaEvent.put("type", event.getType());
        kafkaEvent.put("eventId", event.getEventId());
        kafkaEvent.put("executeAt", System.currentTimeMillis());
        kafkaEvent.put("payload", event.getPayload());

        kafkaTemplate.send(
                "event.realtime",
                event.getEventId(),
                objectMapper.writeValueAsString(kafkaEvent)
        );
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
