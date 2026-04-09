package com.minh.realtime_gateway.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.common.kafka.KafkaUtils;
import com.minh.realtime_gateway.DTOs.WsMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendGameAnswer(WsMessage ws) {
        if (Objects.isNull(ws)) {
            log.error("Cannot send null message to Kafka");
            return;
        }
        try {
            String shardKey = KafkaUtils.generateShardKey(ws.getEventId());
            kafkaTemplate.send(
                    "event.game.answer",
                    shardKey,
                    objectMapper.writeValueAsString(ws)
            );
            log.info("Sent game answer event to Kafka: {} with shard key: {}", ws, shardKey);
        } catch (Exception e) {
            log.error("Failed to send game answer event to Kafka", e);
        }
    }
}
