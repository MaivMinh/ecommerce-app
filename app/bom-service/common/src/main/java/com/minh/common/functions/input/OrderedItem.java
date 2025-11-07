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
    private String name;
    private Integer quantity;
    private Double price;
    private String cover;
    private String colorName;
    private String size;
}
