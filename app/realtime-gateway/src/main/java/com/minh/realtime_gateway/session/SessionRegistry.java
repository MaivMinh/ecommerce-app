package com.minh.realtime_gateway.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionRegistry {
    private final ObjectMapper objectMapper;
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void addSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session.getId(), session);
    }

    public List<WebSocketSession> getAllSessionIds() {
        return sessions.values().stream().toList();
    }

    public void broadcast(Object payload) {
        try {
            String payloadStr = objectMapper.writeValueAsString(payload);
            log.info("Broadcasting message to all sessions: {}", payloadStr);
        }   catch (JsonProcessingException e) {
            log.error("Broadcast message failed: {}", e.getMessage());
            return;
        }

        List<WebSocketSession> sessions = getAllSessionIds();
        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) {
                log.warn("Session {} is not open, skipping broadcast", session.getId());
                continue;
            }

            try {
                synchronized (session) {
                    session.sendMessage(
                            new TextMessage(
                                    Objects.requireNonNull(objectMapper.writeValueAsString(payload))
                            )
                    );
                }
            } catch (Exception e) {
                log.error("Broadcast message to session {} failed: {}", session.getId(), e.getMessage(), e);
            }
        }
    }
}