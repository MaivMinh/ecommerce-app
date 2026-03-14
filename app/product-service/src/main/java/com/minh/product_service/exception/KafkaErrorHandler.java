package com.minh.product_service.exception;


import com.minh.common.commands.ReleaseProductCommand;
import com.minh.common.commands.ReserveProductCommand;
import com.minh.common.events.ProductReservationFailedEvent;
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
    public DefaultErrorHandler productKafkaErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {

        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(3);
        backOff.setInitialInterval(2000L);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(15000L);

        return new DefaultErrorHandler(
                (record, exception) -> {
                    Object message = record.value();
                    if (message instanceof ReserveProductCommand command) {
                        log.info("Processing failed for ReserveProductCommand with sagaId: {}. Exception: {}", command.getSagaId(), exception.getMessage());
                        ProductReservationFailedEvent event = ProductReservationFailedEvent.builder()
                                .orderId(command.getOrderId())
                                .username(command.getUsername())
                                .errorMsg(exception.getMessage())
                                .build();
                        kafkaTemplate.send(KafkaTopics.PRODUCT_RESERVATION_FAILED, command.getSagaId(), event);
                        log.info("Failed to process ReserveProductCommand after retries. Sent ProductReservationFailedEvent to Kafka. Message: {}", message);
                    }   else if (message instanceof ReleaseProductCommand command) {
                        log.info("Processing failed for ReleaseProductCommand with sagaId: {}. Exception: {}", command.getSagaId(), exception.getMessage());
                        kafkaTemplate.send(
                                KafkaTopics.GLOBAL_TECHNICAL_DLT,
                                command.getSagaId(),
                                command
                        );
                        log.info("Failed to process RefundProcessedPaymentCommand after retries. Sent to DLT. Message: {}", message);
                    } else {
                        log.error("Received unknown message type: {}", message.getClass().getName());
                    }
                },
                backOff
        );
    }
}
