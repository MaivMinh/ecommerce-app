package com.minh.promotion_service.command.controller;

import com.minh.common.constants.ResponseMessages;
import com.minh.common.response.ResponseData;
import com.minh.promotion_service.command.commands.CreatePromotionCommand;
import com.minh.promotion_service.payload.request.PromotionCreateReq;
import com.minh.promotion_service.query.queries.SearchPromotionsQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
/**/
}