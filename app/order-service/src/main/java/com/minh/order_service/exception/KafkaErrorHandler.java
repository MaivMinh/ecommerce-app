package com.minh.order_service.exception;


import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.backoff.FixedBackOff;

@Service
public class KafkaErrorHandler {

    @Bean
    public DefaultErrorHandler orderKafkaErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        return new DefaultErrorHandler(
                (record, ex) -> kafkaTemplate.send(
                        "ORCHESTRATOR_DLT",
                        (String) record.key(),
                        record.value()
                ),
                new FixedBackOff(1000L, 3)
        );
    }
}
