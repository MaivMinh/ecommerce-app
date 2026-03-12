package com.minh.payment_service.service.impl;

import com.minh.common.commands.ProcessPaymentCommand;
import com.minh.common.commands.RefundProcessedPaymentCommand;
import com.minh.common.constants.ErrorCode;
import com.minh.common.events.PaymentProcessedEvent;
import com.minh.common.events.PaymentRefundedEvent;
import com.minh.common.events.SagaEvent;
import com.minh.common.kafka.KafkaTopics;
import com.minh.common.message.MessageCommon;
import com.minh.common.utils.AppUtils;
import com.minh.payment_service.entity.Payment;
import com.minh.payment_service.enums.PaymentStatus;
import com.minh.payment_service.repository.PaymentRepository;
import com.minh.payment_service.service.PaymentProcessingService;
import com.minh.payment_service.service.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProcessingServiceImpl implements PaymentProcessingService {
    private final PaymentRepository paymentRepository;
    private final MessageCommon messageCommon;
    private final KafkaTemplate<String, SagaEvent> kafkaTemplate;
    private PaymentStrategy paymentStrategy;

    @Override
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    @Override
    public void processPayment(ProcessPaymentCommand command) {
        try {
            command.setPaymentId(AppUtils.generateUUIDv7());
            paymentStrategy.pay(command);
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
            kafkaTemplate.send(KafkaTopics.PAYMENT_PROCESSED,
                    command.getSagaId(),
                    event
            );

        } catch (RuntimeException e) {
            log.error("Payment processing failed for sagaId: {}. Error: {}", command.getSagaId(), e.getMessage());
            PaymentRefundedEvent event = PaymentRefundedEvent.builder()
                    .orderId(command.getOrderId())
                    .username(command.getUsername())
                    .errorMsg(e.getMessage())
                    .build();
            event.setSagaId(command.getSagaId());
            event.setTimestamp(command.getTimestamp());
            kafkaTemplate.send(KafkaTopics.PAYMENT_REFUNDED,
                    command.getSagaId(),
                    event
            );
        }
    }

    @Override
    public void rollbackProcessedPayment(RefundProcessedPaymentCommand command) {
        Payment payment = paymentRepository.findById(command.getPaymentId()).orElseThrow(
                () -> new RuntimeException(messageCommon.getMessage(ErrorCode.Payment.PAYMENT_FAILED, command.getPaymentId()))
        );
        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
    }

    @Override
    public void refundPayment(RefundProcessedPaymentCommand command) {
        if (!StringUtils.hasText(command.getSagaId()) || !StringUtils.hasText(command.getPaymentId())) {
            log.error("Invalid refund payment command: missing sagaId or paymentId");
            return;
        }

        try {
            paymentStrategy.refund(command);
            PaymentRefundedEvent event = PaymentRefundedEvent.builder()
                    .orderId(command.getOrderId())
                    .username(command.getUsername())
                    .build();
            event.setSagaId(command.getSagaId());
            event.setTimestamp(command.getTimestamp());
            kafkaTemplate.send(KafkaTopics.PAYMENT_REFUNDED,
                    command.getSagaId(),
                    event
            );
        } catch (RuntimeException e) {
            log.error("Payment refund failed for sagaId: {}. Error: {}", command.getSagaId(), e.getMessage());
//            PaymentRefundedEvent event = PaymentRefundedEvent.builder()
//                    .orderId(command.getOrderId())
//                    .username(command.getUsername())
//                    .errorMsg(e.getMessage())
//                    .build();
//            event.setSagaId(command.getSagaId());
//            event.setTimestamp(command.getTimestamp());
//            kafkaTemplate.send(KafkaTopics.PAYMENT_REFUNDED,
//                    command.getSagaId(),
//                    event
//            );
        }
    }
}
