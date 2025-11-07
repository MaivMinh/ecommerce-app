package com.minh.order_service.payload.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariantRes {
    private String id;
    private String name;
    private String slug;
    private String size;
    private String colorName;
    private String colorHex;
    private String cover;
    private Double price;
    private Double originalPrice;
}