package com.minh.realtime_gateway.config;

import event_service.EventServiceGrpc;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
public class GrpcClientConfig {
    private final TimeLimiterRegistry timeLimiterRegistry;
    private final Environment env;

    @Bean
    public TimeLimiter eventServiceTimeLimiter() {
        return timeLimiterRegistry.timeLimiter("event-service");
    }

    @Bean
    public EventServiceGrpc.EventServiceStub eventServiceStub() {
        String address = env.getProperty("EVENT_GRPC_SERVER", "localhost:9097");
        return EventServiceGrpc.newStub(ManagedChannelBuilder.forTarget(address).usePlaintext().keepAliveWithoutCalls(true).build());
    }

}
