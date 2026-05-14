package com.minh.realtime_gateway.config;

import event_service.EventServiceGrpc;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
public class GrpcClientConfig {
    private final TimeLimiterRegistry timeLimiterRegistry;

    @Value("${grpc.server.event-service:localhost:9097}")
    private String eventServiceAddress;

    @Bean
    public TimeLimiter eventServiceTimeLimiter() {
        return timeLimiterRegistry.timeLimiter("event-service");
    }

    @Bean
    public EventServiceGrpc.EventServiceStub eventServiceStub() {
        return EventServiceGrpc.newStub(ManagedChannelBuilder.forTarget(eventServiceAddress).usePlaintext().keepAliveWithoutCalls(true).build());
    }

}
