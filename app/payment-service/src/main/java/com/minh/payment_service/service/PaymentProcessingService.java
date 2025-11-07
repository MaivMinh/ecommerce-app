package com.minh.payment_service.service;

import com.minh.common.events.PaymentProcessedEvent;
import com.minh.common.events.PaymentProcessedRollbackEvent;

public interface PaymentProcessingService {
    void processPayment(PaymentProcessedEvent event);

    void rollbackProcessedPayment(PaymentProcessedRollbackEvent event);
}
