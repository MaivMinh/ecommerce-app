package com.minh.payment_service.service.impl;

import com.minh.common.constants.ErrorCode;
import com.minh.common.events.PaymentProcessedEvent;
import com.minh.common.events.PaymentProcessedRollbackEvent;
import com.minh.common.message.MessageCommon;
import com.minh.common.utils.AppUtils;
import com.minh.payment_service.enums.PaymentStatus;
import com.minh.payment_service.query.DTOs.PaymentMethodDto;
import com.minh.payment_service.query.entity.Payment;
import com.minh.payment_service.repository.PaymentRepository;
import com.minh.payment_service.service.PaymentMethodService;
import com.minh.payment_service.service.PaymentProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentProcessingServiceImpl implements PaymentProcessingService {
    private final PaymentRepository paymentRepository;
    private final PaymentMethodService paymentMethodService;
    private final MessageCommon messageCommon;

    @Override
    public void processPayment(PaymentProcessedEvent event) {
        try {
            PaymentMethodDto method = paymentMethodService.findById(event.getPaymentMethodId());
            if (method == null) {
                throw new RuntimeException(messageCommon.getMessage(ErrorCode.PaymentMethod.NOT_FOUND, event.getPaymentMethodId()));
            }

            Payment payment = new Payment();
            payment.setId(event.getPaymentId());
            payment.setOrderId(event.getOrderId());
            payment.setTotal(event.getTotal());
            payment.setPaymentMethodId(event.getPaymentMethodId());
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setCurrency(event.getCurrency());
            payment.setTransactionId(AppUtils.generateUUIDv7());
            paymentRepository.save(payment);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.Payment.PAYMENT_FAILED, event.getPaymentId()));
        }
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
