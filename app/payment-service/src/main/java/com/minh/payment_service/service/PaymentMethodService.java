package com.minh.payment_service.service;

import com.minh.payment_service.command.events.PaymentMethodCreatedEvent;
import com.minh.payment_service.command.events.PaymentMethodDeletedEvent;
import com.minh.payment_service.command.events.PaymentMethodUpdatedEvent;
import com.minh.payment_service.payload.response.ResponseData;
import com.minh.payment_service.query.DTOs.PaymentMethodDto;
import com.minh.payment_service.query.queries.SearchPaymentMethodsQuery;

public interface PaymentMethodService {

    /**
     * Hàm tạo phương thức thanh toán.
     *
     * @param event: sự kiện chứa thông tin phương thức thanh toán mới.
     */
    void createPaymentMethod(PaymentMethodCreatedEvent event);

    ResponseData getPaymentMethods(SearchPaymentMethodsQuery query);

    void updatePaymentMethod(PaymentMethodUpdatedEvent event);

    void deletePaymentMethod(PaymentMethodDeletedEvent event);

    PaymentMethodDto findById(String paymentMethodId);
}
