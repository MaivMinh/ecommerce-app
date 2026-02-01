package com.minh.payment_service.query.projection;

import com.minh.common.events.PaymentProcessedEvent;
import com.minh.common.events.PaymentProcessedRollbackEvent;
import com.minh.payment_service.enums.PaymentProvider;
import com.minh.payment_service.factory.PaymentStrategyFactory;
import com.minh.payment_service.service.PaymentProcessingService;
import com.minh.payment_service.service.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ProcessingGroup(value = "payment-group")
public class PaymentProcessingProjection {
    private final PaymentProcessingService paymentProcessingService;
    private final PaymentStrategyFactory paymentStrategyFactory;

    @EventHandler
    public void on(PaymentProcessedEvent event) {
        PaymentProvider provider = PaymentProvider.valueOf(event.getPaymentMethod());
        PaymentStrategy strategy = paymentStrategyFactory.getPaymentStrategy(provider);
        paymentProcessingService.setPaymentStrategy(strategy);
        paymentProcessingService.processPayment(event);
    }
    @EventHandler
    public void on(PaymentProcessedRollbackEvent event) {
        paymentProcessingService.rollbackProcessedPayment(event);
    }
}
