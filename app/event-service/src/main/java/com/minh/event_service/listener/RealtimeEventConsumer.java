package com.minh.event_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.event_service.DTO.WsMessage;
import com.minh.event_service.service.GameLogicHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class RealtimeEventConsumer {
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final GameLogicHandler gameLogicHandler;

    @KafkaListener(
            topics = "event.game.answer",
            groupId = "realtime-gateway"
    )
    public void consume(String message) {
        try {
            WsMessage event =
                    objectMapper.readValue(message, WsMessage.class);

            gameLogicHandler.handlePlayAnswer(event);

        } catch (Exception e) {
            log.error("Consume realtime event failed", e);
        }
    }
}
