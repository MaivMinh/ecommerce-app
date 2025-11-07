package com.minh.product_service.query.queries;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class FindProductsQuery {
    private int page;
    private int size;
}
