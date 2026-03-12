package com.minh.payment_service.controller;

import com.minh.payment_service.payload.request.CreatePaymentMethodRequest;
import com.minh.payment_service.payload.request.SearchPaymentMethodsQuery;
import com.minh.payment_service.payload.request.UpdatePaymentMethodRequest;
import com.minh.payment_service.payload.response.ResponseData;
import com.minh.payment_service.service.PaymentMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/payment-methods")
@Validated
@RequiredArgsConstructor
public class PaymentMethodController {
    private final PaymentMethodService service;

    @PostMapping(value = "")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> createPaymentMethod(@RequestBody CreatePaymentMethodRequest request) {
        service.createPaymentMethod(request);
        return ResponseEntity.status(200).body(null);
    }

    @PutMapping(value = "")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> updatePaymentMethod(@RequestBody @Valid UpdatePaymentMethodRequest request) {
        service.updatePaymentMethod(request);
        return ResponseEntity.status(200).body(null);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> deletePaymentMethod(@PathVariable("id") String id) {
        service.deletePaymentMethod(id);
        return ResponseEntity.status(200).body(null);
    }

    @PostMapping(value = "/search")
    public ResponseEntity<ResponseData> getPaymentMethods(@RequestBody SearchPaymentMethodsQuery query) {
        ResponseData response = service.getPaymentMethods(query);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
