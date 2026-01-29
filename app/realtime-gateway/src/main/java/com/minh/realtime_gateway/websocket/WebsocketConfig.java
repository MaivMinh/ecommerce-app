package com.minh.realtime_gateway.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebsocketConfig implements WebSocketConfigurer {
    private final GameWebsocketHandler gameWebsocketHandler;
    private final GatewayHandshakeInterceptor gatewayHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gameWebsocketHandler, "/ws/events/**", "/ws/events", "/events", "/events/**")
                .addInterceptors(gatewayHandshakeInterceptor)
                .setAllowedOrigins("*");
    }
}