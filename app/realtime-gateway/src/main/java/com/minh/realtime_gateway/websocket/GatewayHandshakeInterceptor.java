package com.minh.realtime_gateway.websocket;

import com.minh.common.utils.AppUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayHandshakeInterceptor implements HandshakeInterceptor {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("Starting websocket handshake...");
        String username = AppUtils.getUsername();
        log.info("Received handshake request from user: {}", username);
        if (!StringUtils.hasText(username)) {
            log.error("Websocket handshake rejected: missing or invalid username.");
            return false; /// rejected.
        }

        /// Check either user is registered or not could be done here.
        String query = request.getURI().getQuery();
        String eventId = null;
        if (query != null && query.contains("eventId=")) {
            eventId = query.split("eventId=")[1].split("&")[0];
        }
        if (!StringUtils.hasText(eventId)) {
            log.error("Websocket handshake rejected: missing or invalid eventId.");
            return false; /// rejected.
        }

        String redisKeyCheck = "event:attendance:" + eventId + ":user:" + username; /// Chỗ này thì khi thêm key vào Redis, thì có thể truyền value là startTime dùng để kiểm tra xem thời gian mở kết nối có hợp lệ hay không.
        Boolean isRegistered = redisTemplate.hasKey(redisKeyCheck);
        if (!isRegistered) {
            log.error("Websocket handshake rejected: user {} is not registered for event {}", username, eventId);
            return false; /// rejected.
        }

        attributes.put("username", username);
        log.info("Websocket handshake accepted for user: {}", username);
        return true; // accepted.
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}
