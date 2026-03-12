package com.minh.product_service.payload.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FindCategoryBySlug {
    private String slug;
}
