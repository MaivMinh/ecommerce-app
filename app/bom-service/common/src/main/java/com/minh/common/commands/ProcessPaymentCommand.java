package com.minh.common.commands;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessPaymentCommand {
    @TargetAggregateIdentifier
    private String paymentId;
    private String orderId;
    private String orderPromotionId;
    private String reserveProductId;
    private Double total;
    private String currency;
    private String paymentMethodId;
    private String username;
    private String productId;
}
