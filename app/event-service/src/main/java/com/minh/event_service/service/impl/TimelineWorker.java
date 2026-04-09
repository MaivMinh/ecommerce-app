package com.minh.event_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.Duration;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimelineWorker {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String TIMELINE_KEY = "event:timeline";
    private static final String LOCK_PREFIX = "event:lock:";
    private static final int BATCH_SIZE = 20;
    private final TimelineProcessor timelineProcessor;

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
                    Double score = redisTemplate.opsForZSet().score(TIMELINE_KEY, raw); /// Kiểm tra xem event đã được xử lý bởi instance khác chưa.
                    if (score == null) {
                        log.info("Event already processed by another instance, skipping");
                        continue;
                    }
                    timelineProcessor.process(rawString);
                    redisTemplate.opsForZSet().remove(TIMELINE_KEY, raw);
                } catch (Exception e) {
                    log.error("Process timeline event failed", e);
                } finally {
                    unlock(rawString);
                }
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
