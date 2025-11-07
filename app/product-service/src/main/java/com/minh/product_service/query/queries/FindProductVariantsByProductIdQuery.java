package com.minh.product_service.query.queries;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class FindProductVariantsByProductIdQuery {
    private String productId;
}
