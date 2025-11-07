package com.minh.common.commands;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RollbackCreateOrderCommand {
    @TargetAggregateIdentifier
    private String orderId;
    private String errorMsg;
}
