package com.minh.payment_service.strategy;

import com.minh.common.commands.ProcessPaymentCommand;
import com.minh.common.commands.RefundProcessedPaymentCommand;
import com.minh.common.constants.ErrorCode;
import com.minh.common.events.PaymentProcessedEvent;
import com.minh.common.message.MessageCommon;
import com.minh.common.utils.AppUtils;
import com.minh.payment_service.enums.PaymentProvider;
import com.minh.payment_service.enums.PaymentStatus;
import com.minh.payment_service.payload.response.PaymentResponse;
import com.minh.payment_service.DTOs.PaymentMethodDto;
import com.minh.payment_service.entity.Payment;
import com.minh.payment_service.repository.PaymentRepository;
import com.minh.payment_service.service.AbstractPaymentStrategy;
import com.minh.payment_service.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service("VNPAY")
@RequiredArgsConstructor
public class VNPayStrategy extends AbstractPaymentStrategy {
    private final PaymentMethodService paymentMethodService;
    private final MessageCommon messageCommon;
    private final PaymentRepository paymentRepository;

    @Override
    public PaymentProvider getProvider() {
        return PaymentProvider.VNPAY;
    }

    @Override
    protected PaymentResponse makePayment(ProcessPaymentCommand command) {
        /// Logic triển khai thanh toán qua VNPay.
        log.info("Processing payment via VNPay for request: {}", command);

        String paymentId = AppUtils.generateUUIDv7();
        try {
            PaymentMethodDto method = paymentMethodService.findByCode(command.getPaymentMethod());
            if (method == null) {
                throw new RuntimeException(messageCommon.getMessage(ErrorCode.PaymentMethod.NOT_FOUND, command.getPaymentMethod()));
            }

            Payment payment = new Payment();
            payment.setId(paymentId);
            payment.setOrderId(command.getOrderId());
            payment.setTotal(command.getTotal());
            payment.setPaymentMethodId(method.getId());
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setCurrency(command.getCurrency());
            payment.setTransactionId(AppUtils.generateUUIDv7());
            paymentRepository.save(payment);

            return PaymentResponse.builder()
                    .paymentId(paymentId)
                    .status(HttpStatus.OK.value())
                    .message("Payment processed successfully via VNPay.")
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.Payment.PAYMENT_FAILED, paymentId));
        }
    }

    @Override
    protected void makeRefund(RefundProcessedPaymentCommand command) {

    }
}
