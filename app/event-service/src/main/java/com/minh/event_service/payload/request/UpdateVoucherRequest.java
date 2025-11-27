package com.minh.event_service.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateVoucherRequest {
    @NotBlank
    private String id;
    @NotBlank
    private String campaignId;
    @NotBlank
    private String code;
    private Double discountPercentage;
    private Double value;
    private Double maxValue;
    @NotNull
    private String expirationDate;
}
