package com.minh.payment_service.service.impl;

import com.minh.common.constants.ResponseMessages;
import com.minh.payment_service.query.entity.Payment;
import com.minh.payment_service.repository.PaymentRepository;
import com.minh.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import payment_service.GetPaymentStatusRequest;
import payment_service.GetPaymentStatusResponse;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    @Override
    public GetPaymentStatusResponse getPaymentStatus(GetPaymentStatusRequest request) {
        if (!StringUtils.hasText(request.getOrderId()))
        {
            return GetPaymentStatusResponse.newBuilder()
                    .setStatus(400)
                    .setMessage("Không được để trống orderId")
                    .build();
        }

        Payment payment = paymentRepository.findByOrderId((request.getOrderId()));
        if (Objects.isNull(payment)) {
            return GetPaymentStatusResponse.newBuilder()
                    .setStatus(404)
                    .setMessage("Không tìm thấy payment với orderId: " + request.getOrderId())
                    .build();
        }

        return GetPaymentStatusResponse.newBuilder()
                .setStatus(200)
                .setMessage(ResponseMessages.SUCCESS)
                .setPaymentStatus(payment.getStatus().name())
                .build();
    }
}
