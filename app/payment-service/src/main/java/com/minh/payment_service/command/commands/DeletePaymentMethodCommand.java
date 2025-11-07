package com.minh.payment_service.command.commands;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeletePaymentMethodCommand {
    @TargetAggregateIdentifier
    private String id;
}
