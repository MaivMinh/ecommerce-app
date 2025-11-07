package com.minh.product_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductSearchDTO {
    private String id;
    private String name;
    private String slug;
    private String description;
    private String cover;
    private List<String> images;
    private Double price;
    private Double originalPrice;
    private Long soldItems;
    private Double rating;
    private String status;
    private Boolean isFeatured;
    private Boolean isNew;
    private Boolean isBestseller;
}
