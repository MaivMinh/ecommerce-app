package com.minh.product_service.query.queries;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FindNewestProductsQuery {
    private Integer size;
    private Integer page;
}
