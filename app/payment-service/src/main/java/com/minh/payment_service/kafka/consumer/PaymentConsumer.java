package com.minh.payment_service.kafka.consumer;

import com.minh.common.commands.ProcessPaymentCommand;
import com.minh.common.commands.RefundProcessedPaymentCommand;
import com.minh.common.kafka.KafkaTopics;
import com.minh.payment_service.service.PaymentProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentConsumer {
    private final PaymentProcessingService service;

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_PROCESS,
            groupId = "payment-service"
    )
    @Transactional
    public void handleProcessPaymentCommand(ProcessPaymentCommand command) {
        service.processPayment(command);
    }

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_REFUNDED,
            groupId = "payment-service"
    )
    @Transactional
    public void handleRefundProcessedPaymentCommand(RefundProcessedPaymentCommand command) {
        service.rollbackProcessedPayment(command);
    }

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_REFUND,
            groupId = "payment-service"
    )
    @Transactional
    public void handleRefundPaymentCommand(RefundProcessedPaymentCommand command) {
        service.refundPayment(command);
    }
}
