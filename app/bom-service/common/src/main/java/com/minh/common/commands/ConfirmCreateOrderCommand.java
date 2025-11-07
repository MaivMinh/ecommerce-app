package com.minh.common.commands;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfirmCreateOrderCommand {
    @TargetAggregateIdentifier
    private String orderId;
    private String paymentId;
    private String reserveProductId;
    private String orderPromotionId;
    private String username;
    private String productId;
}
