package com.minh.common.functions.input;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderedItem {
    private String id;
    private String productVariantId;
    private Integer quantity;
    private Double price;
    private String name;
    private String cover;
    private String colorName;
    private String size;
    private String linkToProduct;
}
