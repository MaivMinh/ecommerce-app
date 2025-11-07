package com.minh.payment_service.command.commands;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentMethodCommand {
    @TargetAggregateIdentifier
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
