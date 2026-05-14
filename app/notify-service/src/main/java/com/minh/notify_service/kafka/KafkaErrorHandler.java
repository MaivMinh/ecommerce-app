package com.minh.notify_service.kafka;

import com.minh.common.functions.input.NotifyOrderCancelledEvent;
import com.minh.common.functions.input.NotifyOrderCompletedEvent;
import com.minh.common.kafka.KafkaTopics;
import com.minh.notify_service.outbox.OutboxMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaErrorHandler {

    @Bean
    public DefaultErrorHandler paymentKafkaErrorHandler(KafkaTemplate<String, Object> kafkaTemplate, OutboxMessageService outboxMessageService) {

        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(3);
        backOff.setInitialInterval(2000L);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(15000L);

        return new DefaultErrorHandler(
                (record, exception) -> {
                    Object message = record.value();
                    if (message instanceof NotifyOrderCompletedEvent event) {
                        log.info("Processing failed for NotifyOrderCompletedEvent with orderID: {}. Exception: {}", event.getOrderId(), exception.getMessage());
                        outboxMessageService.store(record.topic(), event, event.getOrderId(), event.getClass().getName());
                    } else if (message instanceof NotifyOrderCancelledEvent event) {
                        log.info("Processing failed for NotifyOrderCancelledEvent with orderID: {}. Exception: {}", event.getOrderId(), exception.getMessage());
                        outboxMessageService.store(record.topic(), event, event.getOrderId(), event.getClass().getName());
                    } else {
                        log.error("Received unknown event type: {}", message.getClass().getName());
                    }
                },
                backOff
        );
    }
}