package com.minh.product_service.query.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SearchProductDTO {
    private List<String> categoryIds;
    private Double minPrice;
    private Double maxPrice;
    private Double rating;
    private Boolean isBestseller;
    private Boolean isNew;
    private Boolean isFeatured;
    private String status;
}
