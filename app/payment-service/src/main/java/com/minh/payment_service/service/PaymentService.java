package com.minh.payment_service.service;


import payment_service.GetPaymentStatusRequest;
import payment_service.GetPaymentStatusResponse;

public interface PaymentService {
    GetPaymentStatusResponse getPaymentStatus(GetPaymentStatusRequest request);
}
