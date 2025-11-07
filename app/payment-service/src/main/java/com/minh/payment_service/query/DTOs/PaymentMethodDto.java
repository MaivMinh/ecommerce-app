package com.minh.payment_service.query.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentMethodDto {
    private String id;
    private String name;
    private String code;
    private String type;
    private String provider;
    private String currency;
    private String iconUrl;
    private String description;
    private Boolean isActive;
}
