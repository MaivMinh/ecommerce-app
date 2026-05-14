package com.minh.payment_service.kafka.consumer;

import com.minh.common.commands.ProcessPaymentCommand;
import com.minh.common.commands.RefundProcessedPaymentCommand;
import com.minh.common.kafka.KafkaTopics;
import com.minh.payment_service.enums.PaymentProvider;
import com.minh.payment_service.factory.PaymentStrategyFactory;
import com.minh.payment_service.service.PaymentProcessingService;
import com.minh.payment_service.service.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentConsumer {
    private final PaymentProcessingService service;
    private final PaymentStrategyFactory factory;

    private PaymentStrategy getPaymentStrategy(String paymentMethod) {
        PaymentProvider provider = PaymentProvider.valueOf(paymentMethod);
        return factory.getPaymentStrategy(provider);
    }

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_PROCESS,
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void handleProcessPaymentCommand(ProcessPaymentCommand command) {
        PaymentStrategy strategy = this.getPaymentStrategy(command.getPaymentMethod());
        service.processPayment(command, strategy);
    }


    @KafkaListener(
            topics = KafkaTopics.PAYMENT_REFUND,
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void handleRefundPaymentCommand(RefundProcessedPaymentCommand command) {
        PaymentStrategy strategy = this.getPaymentStrategy(command.getPaymentMethod());
        service.refundPayment(command, strategy);
    }
}
