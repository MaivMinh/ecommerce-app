package com.minh.payment_service.config;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GrpcClientConfig {
    private final TimeLimiterRegistry timeLimiterRegistry;

    @Bean
    public TimeLimiter supportServiceTimeLimiter() {
        return timeLimiterRegistry.timeLimiter("support-service");
    }
}
