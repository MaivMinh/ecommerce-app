package com.minh.payment_service.service;

import com.minh.common.events.PaymentProcessedEvent;
import com.minh.payment_service.enums.PaymentProvider;
import com.minh.payment_service.payload.response.PaymentResponse;

public interface PaymentStrategy {
    PaymentProvider getProvider();
    PaymentResponse pay(PaymentProcessedEvent request);
}
