package com.minh.event_service.service.impl;

import com.minh.event_service.DTO.UserScoreData;
import com.minh.event_service.DTO.WsMessage;
import com.minh.event_service.service.GameLogicHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameLogicHandlerImpl implements GameLogicHandler {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void handlePlayAnswer(WsMessage event) {
        if (Objects.isNull(event) || Objects.isNull(event.getIsCorrect()) || !event.getIsCorrect())     return;
        log.info("Handling play answer for event: {}", event);

        String playerKey = "event:" + event.getEventId() + ":username:" + event.getUsername();  /// Hash Atomic.
        String rankingKey = "event:" + event.getEventId() + ":ranking";    /// Sorted Set.

        /// 1. Update score & correct. Hash Atomic.
        redisTemplate.opsForHash().increment(playerKey,"score", 10);
        redisTemplate.opsForHash().increment(playerKey,"correct", 1);

        /// 2. Update ranking. Sorted Set.
        redisTemplate.opsForZSet().incrementScore(rankingKey,event.getUsername(),10);
    }
}