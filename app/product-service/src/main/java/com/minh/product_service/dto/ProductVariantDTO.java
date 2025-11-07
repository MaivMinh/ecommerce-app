package com.minh.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariantDTO {
    private String id;
    private String productId;
    @NotBlank
    private String size;
    @NotBlank
    private String colorName;
    @NotBlank
    private String colorHex;
    @NotNull
    private Double price;
    @NotNull
    private Double originalPrice;
    @NotNull
    private Integer quantity;
}
