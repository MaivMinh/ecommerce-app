package com.minh.payment_service.strategy;

import com.minh.common.commands.ProcessPaymentCommand;
import com.minh.common.commands.RefundProcessedPaymentCommand;
import com.minh.payment_service.enums.PaymentProvider;
import com.minh.payment_service.payload.response.PaymentResponse;
import com.minh.payment_service.service.AbstractPaymentStrategy;
import com.minh.payment_service.service.PayPalCheckoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("PAYPAL")
@RequiredArgsConstructor
public class PayPalStrategy extends AbstractPaymentStrategy {
    private final PayPalCheckoutService payPalCheckoutService;

    @Override
    public PaymentProvider getProvider() {
        return PaymentProvider.PAYPAL;
    }

    @Override
    protected PaymentResponse makePayment(ProcessPaymentCommand command) {
        log.info("Processing payment via PayPal for request: {}", command);
        return payPalCheckoutService.initiateCheckout(command);
    }

    @Override
    protected void makeRefund(RefundProcessedPaymentCommand command) {
        log.info("Processing refund via PayPal for command: {}", command);
        payPalCheckoutService.refund(command);
    }
}
