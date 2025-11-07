package com.minh.payment_service.query.controller;

import com.minh.payment_service.payload.response.ResponseData;
import com.minh.payment_service.query.queries.SearchPaymentMethodsQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/payment-methods")
@Validated
@RequiredArgsConstructor
public class PaymentMethodQueryController {
    private final QueryGateway gateway;

    @PostMapping(value = "/search")
    public ResponseEntity<ResponseData> getPaymentMethods(@RequestBody SearchPaymentMethodsQuery query) {

        ResponseData response = gateway.query(query, ResponseTypes.instanceOf(ResponseData.class)).join();
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
