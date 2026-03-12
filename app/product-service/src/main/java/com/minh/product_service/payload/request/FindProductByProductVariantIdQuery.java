package com.minh.product_service.payload.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindProductByProductVariantIdQuery {
    private String productVariantId;
}
