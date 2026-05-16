package com.minh.realtime_gateway.websocket;

import com.minh.common.utils.AppUtils;
import com.minh.realtime_gateway.service.KeycloakIntrospectionService;
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
    private final KeycloakIntrospectionService keycloakIntrospectionService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("Starting websocket handshake...");
        log.info("Introspecting HTTP request: {}", request);
        /// thực hiện gửi yêu cầu xác thực token sang cho Keycloak.
        final String query = request.getURI().getQuery();
        String token = null;
        if (query != null && query.contains("token=")) {
            token = query.split("token=")[1].split("&")[0];
        }
        if (!StringUtils.hasText(token)) {
            log.error("Websocket handshake rejected: missing or invalid token.");
            return false; /// rejected.
        }
        Boolean isAuthenticated = keycloakIntrospectionService.introspect(token);
        if (!isAuthenticated) {
            log.error("Websocket handshake rejected: unauthenticated request.");
        }   else log.info("Websocket handshake: token is authenticated.");

        String username = AppUtils.getUsername();
        String eventId = null;
        if (query.contains("eventId=")) {
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
