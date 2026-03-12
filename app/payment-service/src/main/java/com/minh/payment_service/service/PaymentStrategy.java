package com.minh.payment_service.service;

import com.minh.common.commands.ProcessPaymentCommand;
import com.minh.common.commands.RefundProcessedPaymentCommand;
import com.minh.common.events.PaymentProcessedEvent;
import com.minh.payment_service.enums.PaymentProvider;
import com.minh.payment_service.payload.response.PaymentResponse;

public interface PaymentStrategy {
    PaymentProvider getProvider();
    PaymentResponse pay(ProcessPaymentCommand command);

    void refund(RefundProcessedPaymentCommand command);
}
