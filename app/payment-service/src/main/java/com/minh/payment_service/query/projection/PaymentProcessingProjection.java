package com.minh.payment_service.query.projection;

import com.minh.common.events.PaymentProcessedEvent;
import com.minh.common.events.PaymentProcessedRollbackEvent;
import com.minh.payment_service.service.PaymentProcessingService;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ProcessingGroup(value = "payment-group")
public class PaymentProcessingProjection {
    private final PaymentProcessingService paymentProcessingService;

    @EventHandler
    public void on(PaymentProcessedEvent event) {
        paymentProcessingService.processPayment(event);
    }
    @EventHandler
    public void on(PaymentProcessedRollbackEvent event) {
        paymentProcessingService.rollbackProcessedPayment(event);
    }
}
