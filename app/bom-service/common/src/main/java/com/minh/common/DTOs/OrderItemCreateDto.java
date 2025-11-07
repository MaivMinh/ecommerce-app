package com.minh.common.DTOs;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemCreateDto {
    private String id;
    private String orderId;
    private String productVariantId;
    private Integer quantity;
    private Double price;
    private Double total;
}
