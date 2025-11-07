package com.minh.common.commands;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplyPromotionCommand {
    @TargetAggregateIdentifier
    private String orderPromotionId;
    private String reserveProductId;
    private String orderId;
    private String promotionId;
    private String paymentMethodId;
    private Double total;
    private String currency;
    private String username;
    private String productId;
}
