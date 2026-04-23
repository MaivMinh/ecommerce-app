package com.minh.order_service.exception;


import com.minh.common.kafka.KafkaTopics;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.stereotype.Service;

@Service
public class KafkaErrorHandler {

    @Bean
    public DefaultErrorHandler orderKafkaErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {

        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(3);
        backOff.setInitialInterval(2000L);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(15000L);

        return new DefaultErrorHandler(
                (record, ex) -> kafkaTemplate.send(
                        KafkaTopics.GLOBAL_TECHNICAL_DLT,
                        (String) record.key(),
                        record.value()
                ),
                backOff
        );
    }
}
