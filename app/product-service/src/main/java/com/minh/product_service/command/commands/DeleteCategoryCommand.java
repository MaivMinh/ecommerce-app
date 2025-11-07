package com.minh.product_service.command.commands;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Builder
@Data
public class DeleteCategoryCommand {
    @TargetAggregateIdentifier
    private String id;
}
