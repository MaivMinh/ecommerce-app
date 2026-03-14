package com.minh.payment_service.service.impl;

import com.minh.common.commands.ProcessPaymentCommand;
import com.minh.common.commands.RefundProcessedPaymentCommand;
import com.minh.common.events.PaymentFailedEvent;
import com.minh.common.events.PaymentProcessedEvent;
import com.minh.common.events.PaymentRefundedEvent;
import com.minh.common.kafka.KafkaTopics;
import com.minh.common.utils.AppUtils;
import com.minh.payment_service.service.PaymentProcessingService;
import com.minh.payment_service.service.PaymentStrategy;
import com.minh.payment_service.service.ProcessedMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProcessingServiceImpl implements PaymentProcessingService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProcessedMessageService processedMessageService;

    @Override
    public void processPayment(ProcessPaymentCommand command, PaymentStrategy strategy) {
        if (isMessageProcessed(command.getMessageId())) {
            log.info("Refund command with messageId: {} has already been processed. Skipping.", command.getMessageId());
            return;
        }
        try {
            command.setPaymentId(AppUtils.generateUUIDv7());
            strategy.pay(command);
            PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                    .orderId(command.getOrderId())
                    .username(command.getUsername())
                    .currency(command.getCurrency())
                    .paymentId(command.getPaymentId())
                    .paymentMethod(command.getPaymentMethod())
                    .total(command.getTotal())
                    .build();

            event.setSagaId(command.getSagaId());
            event.setTimestamp(command.getTimestamp());
            processedMessageService.store(command);
            kafkaTemplate.send(KafkaTopics.PAYMENT_PROCESSED,
                    command.getSagaId(),
                    event
            );

        } catch (RuntimeException e) {
            log.error("Payment processing failed for sagaId: {}. Error: {}", command.getSagaId(), e.getMessage());
            PaymentFailedEvent event = PaymentFailedEvent.builder()
                    .orderId(command.getOrderId())
                    .username(command.getUsername())
                    .errorMsg(e.getMessage())
                    .build();
            event.setSagaId(command.getSagaId());
            event.setTimestamp(command.getTimestamp());
            kafkaTemplate.send(KafkaTopics.PAYMENT_FAILED,
                    command.getSagaId(),
                    event
            );
        }
    }

    @Override
    public void refundPayment(RefundProcessedPaymentCommand command, PaymentStrategy strategy) {
        if (!StringUtils.hasText(command.getSagaId()) || !StringUtils.hasText(command.getPaymentId())) {
            log.error("Invalid refund payment command: missing sagaId or paymentId");
            return;
        }
        if (isMessageProcessed(command.getMessageId())) {
            log.info("Refund command with messageId: {} has already been processed. Skipping.", command.getMessageId());
            return;
        }

        try {
            strategy.refund(command);
            PaymentRefundedEvent event = PaymentRefundedEvent.builder()
                    .orderId(command.getOrderId())
                    .username(command.getUsername())
                    .paymentMethod(command.getPaymentMethod())
                    .build();
            event.setSagaId(command.getSagaId());
            event.setTimestamp(command.getTimestamp());
            processedMessageService.store(command);
            kafkaTemplate.send(KafkaTopics.PAYMENT_REFUNDED,
                    command.getSagaId(),
                    event
            );
        } catch (RuntimeException e) {
            log.error("Payment refund failed for sagaId: {}. Error: {}", command.getSagaId(), e.getMessage());
            throw e;
        }
    }

    private boolean isMessageProcessed(String messageId) {
        return processedMessageService.isMessageProcessed(messageId);
    }
}
