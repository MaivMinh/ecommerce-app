package com.minh.product_service.command.commands;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class UpdateCategoryCommand {
    @TargetAggregateIdentifier
    private String id;
    private String parentId;
    private String name;
    private String description;
    private String slug;
    private String image;
}
