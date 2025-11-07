package com.minh.product_service.query.queries;

import com.minh.common.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SearchProductQuery {
    private Integer page;
    private Integer size;
    private String sort;
    private List<String> categoryIds;
    private Double minPrice;
    private Double maxPrice;
    private Double rating;
    private Boolean isBestseller;
    private Boolean isNew;
    private Boolean isFeatured;
    private ProductStatus status;
}
