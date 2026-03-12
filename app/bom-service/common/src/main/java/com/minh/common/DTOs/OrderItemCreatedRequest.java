package com.minh.common.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemCreatedRequest {
    private String id;
    @NotBlank
    private String orderId;
    @NotBlank
    private String productVariantId;
    @NotNull
    @Positive
    private Integer quantity;
    @NotNull
    @Positive
    private Double price;
    @NotNull
    @Positive
    private Double total;
}
