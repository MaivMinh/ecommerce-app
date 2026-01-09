package com.minh.realtime_gateway.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.realtime_gateway.DTOs.WsMessage;
import com.minh.realtime_gateway.session.SessionRegistry;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameWebsocketHandler implements WebSocketHandler {
    private final SessionRegistry registry;
    private final ObjectMapper objectMapper;

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
            welcomeMsg.put("message", "Welcome " + username + "!");
            welcomeMsg.put("timestamp", System.currentTimeMillis());

            String json = objectMapper.writeValueAsString(welcomeMsg);
            log.info("📤 Sending welcome message: {}", json);

            session.sendMessage(new TextMessage(json));
            log.info("✅ Welcome message sent successfully");

        } catch (Exception e) {
            log.error("❌ Error sending welcome message", e);
        }
        registry.addSession(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        WsMessage wsMessage = Json.mapper().readValue(message.getPayload().toString(), WsMessage.class);
        log.info("Received message from session {}: {}", session.getId(), wsMessage.toString());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Transport error in session {}: {}", session.getId(), exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("WebSocket connection closed. Session ID: {}, Close Status: {}", session.getId(), closeStatus);
        registry.removeSession(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
