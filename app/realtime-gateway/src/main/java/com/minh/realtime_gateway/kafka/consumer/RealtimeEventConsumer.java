package com.minh.realtime_gateway.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.realtime_gateway.DTOs.RealtimeEvent;
import com.minh.realtime_gateway.session.SessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RealtimeEventConsumer {

    private final ObjectMapper objectMapper;
    private final SessionRegistry sessionRegistry;

    @KafkaListener(
            topics = "event.realtime",
            groupId = "realtime-gateway"
    )
    public void consume(String message) {
        try {
            RealtimeEvent event =
                    objectMapper.readValue(message, RealtimeEvent.class);

            pushToClients(event);

        } catch (Exception e) {
            log.error("Consume realtime event failed", e);
        }
    }

    private void pushToClients(RealtimeEvent event) {
        Map<String, Object> wsMessage = new HashMap<>();
        wsMessage.put("type", event.getType());
        wsMessage.put("payload", event.getPayload());
        wsMessage.put("eventId", event.getEventId());
        wsMessage.put("executedAt", event.getExecuteAt());

        sessionRegistry.broadcast(wsMessage);
    }
}
