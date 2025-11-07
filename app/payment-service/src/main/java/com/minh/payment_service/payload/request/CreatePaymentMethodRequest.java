package com.minh.payment_service.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentMethodRequest {
    private String name;
    private String code;
    private String type;
    private String provider;
    private String currency;
    private String note;
    private String description;
    private Boolean isActive;
    private String iconUrl;
}
