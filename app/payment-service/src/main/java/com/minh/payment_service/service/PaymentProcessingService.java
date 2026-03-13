package com.minh.payment_service.service;

import com.minh.common.commands.ProcessPaymentCommand;
import com.minh.common.commands.RefundProcessedPaymentCommand;

public interface PaymentProcessingService {
    void processPayment(ProcessPaymentCommand command);

    void setPaymentStrategy(PaymentStrategy paymentStrategy);

    void refundPayment(RefundProcessedPaymentCommand command);
}