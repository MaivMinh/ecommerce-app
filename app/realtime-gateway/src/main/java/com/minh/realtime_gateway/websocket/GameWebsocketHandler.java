package com.minh.realtime_gateway.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.realtime_gateway.DTOs.GameEvent;
import com.minh.realtime_gateway.DTOs.RealtimeEvent;
import com.minh.realtime_gateway.DTOs.WsMessage;
import com.minh.realtime_gateway.kafka.producer.GameEventProducer;
import com.minh.realtime_gateway.session.SessionRegistry;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameWebsocketHandler implements WebSocketHandler {
    private final SessionRegistry registry;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final GameEventProducer gameEventProducer;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = (String) session.getAttributes().get("username");

        log.info("=== New WebSocket Connection ===");
        log.info("Session ID: {}", session.getId());
        log.info("Username: {}", username);
        log.info("================================");

        registry.addSession(session);

        // ✅ GỬI WELCOME MESSAGE NGAY SAU KHI KẾT NỐI
        try {
            Map<String, Object> welcomeMsg = new HashMap<>();
            welcomeMsg.put("type", "CONNECTED");
            welcomeMsg.put("message", "Kết nối thành công. Vui lòng đợi trong giây lát!");
            welcomeMsg.put("timestamp", System.currentTimeMillis());

            String json = objectMapper.writeValueAsString(welcomeMsg);
            session.sendMessage(new TextMessage(json));
            log.info("✅ Welcome message sent successfully");

            /// Emit event to Kafka that user has connected.
            Integer participants = this.updateParticipationCount(Boolean.TRUE);
            RealtimeEvent event = RealtimeEvent.builder()
                    .type("PLAYER_PARTICIPATED")
                    .eventId("")
                    .executeAt(System.currentTimeMillis())
                    .payload(objectMapper.valueToTree(Map.of(
                            "username", username,
                            "event", "PLAYER_PARTICIPATED")
                    ))
                    .participants(participants)
                    .build();

            kafkaTemplate.send(
                    "event.player.participate",
                    objectMapper.writeValueAsString(event)
            );
        } catch (Exception e) {
            log.error("❌ Error sending welcome message", e);
        }
    }

    /*
        Hàm thực hiện cập nhật số lượng participants tham gia vào Event hiện tại.
        Hàm này sẽ gửi một sự kiện đến Kafka để cập nhật số lượng người chơi tham gia.
     */
    private Integer updateParticipationCount(Boolean newConnection) {
        Integer result = null;
        String participantsKey = "event:current:participants";
        result = (Integer) redisTemplate.opsForValue().get(participantsKey);
        if (newConnection) {
            if (Objects.isNull(result)) {
                result = 1;
            } else {
                result += 1;
            }
        }   else if (Objects.nonNull(result)) {
            result = Math.max(0, result - 1);
        }
        redisTemplate.opsForValue().set(participantsKey, result);
        return result;
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        WsMessage wsMessage = Json.mapper().readValue(message.getPayload().toString(), WsMessage.class);

        /// Handle incoming messages when game is running.
        if (wsMessage.getType().equals("PLAYER_ANSWER")) {
            gameEventProducer.sendGameAnswer(wsMessage);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Transport error in session {}: {}", session.getId(), exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("WebSocket connection closed. Session ID: {}, Close Status: {}", session.getId(), closeStatus);
        registry.removeSession(session);
        Integer participants = this.updateParticipationCount(Boolean.FALSE);
        RealtimeEvent event = RealtimeEvent.builder()
                .type("PLAYER_LEFT")
                .eventId("")
                .executeAt(System.currentTimeMillis())
                .payload(objectMapper.valueToTree(Map.of(
                        "username", session.getAttributes().get("username"),
                        "event", "PLAYER_LEFT")
                ))
                .participants(participants)
                .build();
        kafkaTemplate.send(
                "event.player.left",
                objectMapper.writeValueAsString(event)
        );
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
