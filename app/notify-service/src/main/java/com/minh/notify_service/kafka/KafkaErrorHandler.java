package com.minh.notify_service.kafka;

import com.minh.common.commands.ProcessPaymentCommand;
import com.minh.common.commands.RefundProcessedPaymentCommand;
import com.minh.common.events.PaymentFailedEvent;
import com.minh.common.functions.input.NotifyOrderCancelledEvent;
import com.minh.common.functions.input.NotifyOrderCompletedEvent;
import com.minh.common.kafka.KafkaTopics;
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
    public DefaultErrorHandler paymentKafkaErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {

        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(3);
        backOff.setInitialInterval(2000L);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(15000L);

        return new DefaultErrorHandler(
                (record, exception) -> {
                    Object message = record.value();
                    if (message instanceof NotifyOrderCompletedEvent event) {
                        log.info("Processing failed for NotifyOrderCompletedEvent with orderID: {}. Exception: {}", event.getOrderId(), exception.getMessage());
                        kafkaTemplate.send(
                                KafkaTopics.NOTIFICATION_FAILED_DLT,
                                event.getOrderId(),
                                event
                        );
                        log.info("Sent NotifyOrderCompletedEvent to DLT after retries. Message: {}", message);
                    } else if (message instanceof NotifyOrderCancelledEvent event) {
                        log.info("Processing failed for NotifyOrderCancelledEvent with orderID: {}. Exception: {}", event.getOrderId(), exception.getMessage());
                        kafkaTemplate.send(
                                KafkaTopics.NOTIFICATION_FAILED_DLT,
                                event.getOrderId(),
                                event
                        );
                        log.info("Sent NotifyOrderCancelledEvent to DLT after retries. Message: {}", message);
                    } else {
                        log.error("Received unknown event type: {}", message.getClass().getName());
                    }
                },
                backOff
        );
    }
}