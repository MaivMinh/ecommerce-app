package com.minh.payment_service.service.impl;

import com.minh.common.constants.ErrorCode;
import com.minh.common.events.PaymentProcessedEvent;
import com.minh.common.events.PaymentProcessedRollbackEvent;
import com.minh.common.message.MessageCommon;
import com.minh.common.utils.AppUtils;
import com.minh.payment_service.enums.PaymentStatus;
import com.minh.payment_service.payload.request.PaymentRequest;
import com.minh.payment_service.query.DTOs.PaymentMethodDto;
import com.minh.payment_service.query.entity.Payment;
import com.minh.payment_service.repository.PaymentRepository;
import com.minh.payment_service.service.PaymentMethodService;
import com.minh.payment_service.service.PaymentProcessingService;
import com.minh.payment_service.service.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentProcessingServiceImpl implements PaymentProcessingService {
    private final PaymentRepository paymentRepository;
    private final MessageCommon messageCommon;
    private PaymentStrategy paymentStrategy;

    @Override
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    @Override
    public void processPayment(PaymentProcessedEvent event) {
        paymentStrategy.pay(event);
    }

    @Override
    public void rollbackProcessedPayment(PaymentProcessedRollbackEvent event) {
        Payment payment = paymentRepository.findById(event.getPaymentId()).orElseThrow(
                () -> new RuntimeException(messageCommon.getMessage(ErrorCode.Payment.PAYMENT_FAILED, event.getPaymentId()))
        );
        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
    }
}
