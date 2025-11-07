package com.minh.common.commands;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmReservedProductCommand {
    @TargetAggregateIdentifier
    private String reserveProductId;
    private String orderPromotionId;
    private String orderId;
    private String paymentId;
    private String username;
    private String productId;
    private String errorMsg;
}
