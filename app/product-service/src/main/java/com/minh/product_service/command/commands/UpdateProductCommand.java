package com.minh.product_service.command.commands;

import com.minh.product_service.dto.ProductVariantDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UpdateProductCommand {
    @TargetAggregateIdentifier
    private String id;
    private String name;
    private String slug;
    private String description;
    private String cover;
    private List<String> images;
    private Double price;
    private Double originalPrice;
    private List<ProductVariantDTO> productVariants;
    private String status;
    private Boolean isFeatured;
    private Boolean isNew;
    private Boolean isBestseller;
    private String categoryId;
}
