package com.minh.payment_service.command.controller;

import com.minh.common.utils.AppUtils;
import com.minh.payment_service.command.commands.CreatePaymentMethodCommand;
import com.minh.payment_service.command.commands.DeletePaymentMethodCommand;
import com.minh.payment_service.command.commands.UpdatePaymentMethodCommand;
import com.minh.payment_service.payload.request.CreatePaymentMethodRequest;
import com.minh.payment_service.payload.request.DeletePaymentMethodRequest;
import com.minh.payment_service.payload.request.UpdatePaymentMethodRequest;
import com.minh.payment_service.payload.response.ResponseData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/api/payment-methods")
@Validated
@RequiredArgsConstructor
public class PaymentMethodCommandController {
    private final CommandGateway gateway;

    @PostMapping(value = "")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> createPaymentMethod(@RequestBody CreatePaymentMethodRequest request) {
        CreatePaymentMethodCommand command = CreatePaymentMethodCommand.builder()
                .id(AppUtils.generateUUIDv7())
                .name(request.getName())
                .description(request.getDescription())
                .code(request.getCode())
                .type(request.getType())
                .provider(request.getProvider())
                .currency(request.getCurrency())
                .iconUrl(request.getIconUrl())
                .note(request.getNote())
                .isActive(request.getIsActive())
                .build();

        gateway.sendAndWait(command, 20000, TimeUnit.MILLISECONDS);
        return ResponseEntity.status(200).body(null);
    }

    @PutMapping(value = "")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> updatePaymentMethod(@RequestBody @Valid UpdatePaymentMethodRequest request) {
        UpdatePaymentMethodCommand command = UpdatePaymentMethodCommand.builder()
                .id(request.getId())
                .name(request.getName())
                .description(request.getDescription())
                .code(request.getCode())
                .type(request.getType())
                .provider(request.getProvider())
                .currency(request.getCurrency())
                .iconUrl(request.getIconUrl())
                .note(request.getNote())
                .isActive(request.getIsActive())
                .build();

        gateway.sendAndWait(command, 20000, TimeUnit.MILLISECONDS);
        return ResponseEntity.status(200).body(null);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> deletePaymentMethod(@PathVariable("id") String id) {
        DeletePaymentMethodCommand command = DeletePaymentMethodCommand.builder()
                .id(id)
                .build();
        gateway.sendAndWait(command, 20000, TimeUnit.MILLISECONDS);
        return ResponseEntity.status(200).body(null);
    }
}
