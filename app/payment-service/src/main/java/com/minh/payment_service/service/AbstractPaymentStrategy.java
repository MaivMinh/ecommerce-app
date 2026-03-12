package com.minh.payment_service.service;

import com.minh.common.commands.ProcessPaymentCommand;
import com.minh.common.commands.RefundProcessedPaymentCommand;
import com.minh.common.events.PaymentProcessedEvent;
import com.minh.payment_service.entity.Payment;
import com.minh.payment_service.payload.response.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public abstract class AbstractPaymentStrategy implements PaymentStrategy{
    @Override
    public final PaymentResponse pay(ProcessPaymentCommand command) {
        validate(command);
        return makePayment(command);
    }

    private void validate(ProcessPaymentCommand command) {
        if (!StringUtils.hasText(command.getOrderId())
                || !StringUtils.hasText(command.getPaymentMethod())
                || !StringUtils.hasText(command.getProductId())
                || !StringUtils.hasText(command.getUsername())) {
            log.error("Payment service: Tham số truyền vào không hợp lệ: {}", command);
            throw new IllegalArgumentException("Invalid payment request: missing required fields.");
        }
    }
    protected abstract PaymentResponse makePayment(ProcessPaymentCommand command);

    @Override
    public final void refund(RefundProcessedPaymentCommand command) {   /// Khi đặt final ở đây thì không cho phép các class con override lại phương thức này. Điều này có nghĩa là các class con sẽ phải sử dụng logic refund này đã được định sẵn step ở trong, và chỉ có thể override lại phương thức makeRefund() để thực hiện logic refund riêng của từng provider.
        makeRefund(command);
    }
    protected abstract void makeRefund(RefundProcessedPaymentCommand command);
}
