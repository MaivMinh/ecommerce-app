package com.minh.payment_service.service;

import com.minh.common.events.PaymentProcessedEvent;
import com.minh.payment_service.payload.response.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public abstract class AbstractPaymentStrategy implements PaymentStrategy{
    @Override
    public final PaymentResponse pay(PaymentProcessedEvent request) {
        validate(request);
        return makePayment(request);
    }

    private void validate(PaymentProcessedEvent request) {
        if (!StringUtils.hasText(request.getOrderId())
                || !StringUtils.hasText(request.getReserveProductId())
                || !StringUtils.hasText(request.getPaymentMethod())
                || !StringUtils.hasText(request.getProductId())
                || !StringUtils.hasText(request.getUsername())) {
            log.error("Payment service: Tham số truyền vào không hợp lệ: {}", request);
            throw new IllegalArgumentException("Invalid payment request: missing required fields.");
        }
    }
    protected abstract PaymentResponse makePayment(PaymentProcessedEvent request);
}
