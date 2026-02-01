package com.minh.payment_service.strategy;

import com.minh.common.constants.ErrorCode;
import com.minh.common.events.PaymentProcessedEvent;
import com.minh.common.message.MessageCommon;
import com.minh.common.utils.AppUtils;
import com.minh.payment_service.enums.PaymentProvider;
import com.minh.payment_service.enums.PaymentStatus;
import com.minh.payment_service.payload.response.PaymentResponse;
import com.minh.payment_service.query.DTOs.PaymentMethodDto;
import com.minh.payment_service.query.entity.Payment;
import com.minh.payment_service.repository.PaymentRepository;
import com.minh.payment_service.service.AbstractPaymentStrategy;
import com.minh.payment_service.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service("VIETEL_MONEY")
@RequiredArgsConstructor
public class ViettelMoneyStrategy extends AbstractPaymentStrategy {
    private final PaymentMethodService paymentMethodService;
    private final MessageCommon messageCommon;
    private final PaymentRepository paymentRepository;

    @Override
    public PaymentProvider getProvider() {
        return PaymentProvider.VIETTEL_MONEY;
    }

    @Override
    protected PaymentResponse makePayment(PaymentProcessedEvent request) {
        /// Logic triển khai thanh toán qua Viettel Money.
        log.info("Processing payment via Viettel Money for request: {}", request);

        try {
            PaymentMethodDto method = paymentMethodService.findByCode(request.getPaymentMethod());
            if (method == null) {
                throw new RuntimeException(messageCommon.getMessage(ErrorCode.PaymentMethod.NOT_FOUND, request.getPaymentMethod()));
            }

            Payment payment = new Payment();
            payment.setId(request.getPaymentId());
            payment.setOrderId(request.getOrderId());
            payment.setTotal(request.getTotal());
            payment.setPaymentMethodId(request.getPaymentMethod());
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setCurrency(request.getCurrency());
            payment.setTransactionId(AppUtils.generateUUIDv7());
            paymentRepository.save(payment);

            return PaymentResponse.builder()
                    .paymentId(request.getPaymentId())
                    .status(HttpStatus.OK.value())
                    .message("Payment processed successfully via Viettel Money.")
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.Payment.PAYMENT_FAILED, request.getPaymentId()));
        }
    }
}
