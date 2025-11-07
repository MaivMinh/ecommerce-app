package com.minh.payment_service.query.handler;

import com.minh.payment_service.payload.response.ResponseData;
import com.minh.payment_service.query.queries.SearchPaymentMethodsQuery;
import com.minh.payment_service.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentMethodQueryHandler {
    private final PaymentMethodService paymentMethodService;
    @QueryHandler
    public ResponseData handle(SearchPaymentMethodsQuery query) {
        return paymentMethodService.getPaymentMethods(query);
    }
}
