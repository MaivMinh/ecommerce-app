package com.minh.payment_service.service;

import com.minh.payment_service.DTOs.PaymentMethodDto;
import com.minh.payment_service.payload.request.CreatePaymentMethodRequest;
import com.minh.payment_service.payload.request.SearchPaymentMethodsQuery;
import com.minh.payment_service.payload.request.UpdatePaymentMethodRequest;
import com.minh.payment_service.payload.response.ResponseData;

public interface PaymentMethodService {

    /**
     * Hàm tạo phương thức thanh toán.
     * @param request: đầu vào chứa thông tin phương thức thanh toán mới.
     */
    void createPaymentMethod(CreatePaymentMethodRequest request);

    ResponseData getPaymentMethods(SearchPaymentMethodsQuery query);

    void updatePaymentMethod(UpdatePaymentMethodRequest request);

    void deletePaymentMethod(String id);

    PaymentMethodDto findByCode(String paymentMethod);
}
