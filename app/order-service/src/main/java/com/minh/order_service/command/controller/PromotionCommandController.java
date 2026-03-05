package com.minh.order_service.command.controller;

import com.minh.common.constants.ResponseMessages;
import com.minh.common.response.ResponseData;
import com.minh.order_service.command.commands.CreatePromotionCommand;
import com.minh.order_service.payload.request.PromotionCreateReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/api/promotions")
@Validated
@RequiredArgsConstructor
public class PromotionCommandController {
    private final CommandGateway commandGateway;

    @PostMapping(value = "")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> createPromotion(@RequestBody @Valid PromotionCreateReq request) {
        CreatePromotionCommand command = CreatePromotionCommand.builder()
                .promotionId(UUID.randomUUID().toString())
                .code(request.getCode())
                .type(request.getType())
                .discountValue(request.getDiscountValue())
                .minOrderValue(request.getMinOrderValue())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .usageLimit(request.getUsageLimit())
                .usageCount(request.getUsageCount())
                .status(request.getStatus())
                .build();

        commandGateway.sendAndWait(command, 20000
                , TimeUnit.MILLISECONDS);
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(ResponseData.builder().message(ResponseMessages.SUCCESS).build());
    }
}
