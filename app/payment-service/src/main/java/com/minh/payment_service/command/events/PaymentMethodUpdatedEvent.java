package com.minh.payment_service.command.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentMethodUpdatedEvent {
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
