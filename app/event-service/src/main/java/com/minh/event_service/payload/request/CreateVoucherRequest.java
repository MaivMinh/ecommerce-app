package com.minh.event_service.payload.request;

import com.google.type.DateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateVoucherRequest {
    @NotBlank
    private String campaignId;
    @NotBlank
    private String code;
    private BigDecimal discountPercentage;
    private BigDecimal value;
    private BigDecimal maxValue;
    @NotNull
    private DateTime expirationDate;
}