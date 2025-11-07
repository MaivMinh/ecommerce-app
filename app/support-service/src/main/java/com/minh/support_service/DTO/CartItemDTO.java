package com.minh.support_service.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private String id;
    private String cartId;
    @NotNull
    private ProductVariantDTO productVariantDTO;
    @NotNull(message = "Product ID cannot be empty")
    private Integer quantity;
    private String productId;
}
