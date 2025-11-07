package com.minh.payment_service.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePaymentMethodRequest {
    @NotBlank
    private String id;
    private String name;
    private String code;
    private String type;
    private String provider;
    private String currency;
    private String description;
    private String note;
    private Boolean isActive;
    private String iconUrl;
}
