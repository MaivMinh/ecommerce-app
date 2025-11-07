package com.minh.common.DTOs;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReserveProductItem {
    private String productVariantId;
    private Integer quantity;
}
