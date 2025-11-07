package com.minh.common.config;

import org.springframework.boot.actuate.health.StatusAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class HealthStatusConfig {
    @Bean
    @Primary
    public StatusAggregator healthStatusAggregator() {
        return StatusAggregator.getDefault();
    }
}
