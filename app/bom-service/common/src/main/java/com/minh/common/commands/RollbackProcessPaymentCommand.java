package com.minh.common.commands;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RollbackProcessPaymentCommand {
    @TargetAggregateIdentifier
    private String paymentId;
    private String orderId;
    private String reserveProductId;
    private String orderPromotionId;
    private String errorMsg;
}
