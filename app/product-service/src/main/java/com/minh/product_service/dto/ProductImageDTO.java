package com.minh.product_service.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageDTO {
    private String id;
    private String productId;
    private String imageUrl;
}
