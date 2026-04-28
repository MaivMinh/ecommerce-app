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
    protected PaymentResponse makePayment(ProcessPaymentCommand command) {
        log.info("Processing payment via Viettel Money for request: {}", command);

        String paymentId = AppUtils.generateUUIDv7();
        command.setPaymentId(paymentId);
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
                    .message("Payment processed successfully via Viettel Money.")
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.Payment.PAYMENT_FAILED, paymentId));
        }
    }

    @Override
    protected void makeRefund(RefundProcessedPaymentCommand command) {
        log.info("Processing refund via Viettel Money for command: {}", command);
        try {
            Payment payment = paymentRepository.findById(command.getPaymentId())
                    .orElseThrow(() -> new RuntimeException(messageCommon.getMessage(ErrorCode.Payment.NOT_FOUND, command.getPaymentId())));

            if (!payment.getStatus().equals(PaymentStatus.COMPLETED)) {
                log.warn("Payment with ID {} is already in status {}. No refund needed.", command.getPaymentId(), payment.getStatus());
                return; // Không cần thực hiện refund nếu đã failed hoặc refunded
            }

            payment.setStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(payment);
        } catch (Exception e) {
            log.error("Refund processing failed for paymentId: {}. Error: {}", command.getPaymentId(), e.getMessage());
            throw new RuntimeException("Refund processing failed for paymentId: " + command.getPaymentId());
        }
    }
}
