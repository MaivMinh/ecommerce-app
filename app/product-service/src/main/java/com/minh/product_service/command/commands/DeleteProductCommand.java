package com.minh.product_service.command.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class DeleteProductCommand {
    @TargetAggregateIdentifier
    private String id;
}
