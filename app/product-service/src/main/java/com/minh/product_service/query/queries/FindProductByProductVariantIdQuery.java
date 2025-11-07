package com.minh.product_service.query.queries;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindProductByProductVariantIdQuery {
    private String productVariantId;
}
