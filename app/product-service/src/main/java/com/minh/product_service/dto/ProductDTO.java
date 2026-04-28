package com.minh.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private String id;
    @NotBlank(message = "Name is required")
    @Size(min = 5, message = "The length of the products name should be at least 5")
    private String name;
    private String slug;
    private String description;
    private String cover;
    private List<String> images;
    @NotNull(message = "Price is required")
    private Double price;
    @NotNull(message = "Original price is required")
    private Double originalPrice;
    private Long soldItems;
    private Double rating;
    private String status;
    private Boolean isFeatured;
    private Boolean isNew;
    private Boolean isBestseller;
    @NotBlank(message = "Category ID is required")
    private String categoryId;
    private List<ProductVariantDTO> productVariants;
}