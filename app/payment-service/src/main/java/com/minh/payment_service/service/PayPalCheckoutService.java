package com.minh.payment_service.service;

import com.minh.common.commands.ProcessPaymentCommand;
import com.minh.common.commands.RefundProcessedPaymentCommand;
import com.minh.payment_service.payload.response.PaymentResponse;
import com.minh.payment_service.payload.response.ResponseData;

public interface PayPalCheckoutService {
    PaymentResponse initiateCheckout(ProcessPaymentCommand command);

    ResponseData captureOrder(String orderId, String paypalOrderId, String username);

    ResponseData cancelOrder(String orderId, String paypalOrderId, String username);

    void refund(RefundProcessedPaymentCommand command);
}
