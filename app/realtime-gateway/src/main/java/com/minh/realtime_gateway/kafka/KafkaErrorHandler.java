package com.minh.realtime_gateway.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.stereotype.Service;

@Service
public class KafkaErrorHandler {

    @Bean
    public DefaultErrorHandler realtimeGatewayKafkaErrorHandler() {
        return new DefaultErrorHandler(
                (record, ex) -> {
                    // Log lỗi chi tiết
                    System.err.println("Error processing record with key: " + record.key() + ", value: " + record.value());
                    ex.printStackTrace();
                }
        );
    }
}
