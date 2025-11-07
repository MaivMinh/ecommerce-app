package com.minh.order_service.payload.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemRes {
    private String id;
    private ProductVariantRes productVariant;
    private Integer quantity;
    private Double price;
    private Double total;
}