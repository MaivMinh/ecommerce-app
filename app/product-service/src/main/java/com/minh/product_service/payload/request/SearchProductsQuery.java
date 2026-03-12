package com.minh.product_service.payload.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchProductsQuery {
    private String keyword;
    private Integer page;
    private Integer size;
}
