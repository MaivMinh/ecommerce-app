package com.minh.payment_service.exception;


import com.minh.common.commands.ProcessPaymentCommand;
import com.minh.common.commands.RefundProcessedPaymentCommand;
import com.minh.common.events.PaymentFailedEvent;
import com.minh.common.kafka.KafkaTopics;
import com.minh.payment_service.outbox.OutboxMessageService;
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
                    if (message instanceof ProcessPaymentCommand command) {
                        PaymentFailedEvent event = PaymentFailedEvent.builder()
                                .orderId(command.getOrderId())
                                .username(command.getUsername())
                                .errorMsg(exception.getMessage())
                                .build();
                        event.setSagaId(command.getSagaId());
                        event.setTimestamp(command.getTimestamp());
                        event.setMessageId(command.getMessageId());
                        outboxMessageService.store(KafkaTopics.PAYMENT_FAILED, event, event.getClass().getName());
                    } else if (message instanceof RefundProcessedPaymentCommand command) {
                        command.setMessageId(command.getMessageId());
                        outboxMessageService.store(KafkaTopics.GLOBAL_TECHNICAL_DLT, command, command.getClass().getName());
                    } else {
                        log.error("Received unknown message type: {}", message.getClass().getName());
                    }
                },
                backOff
        );
    }
}
