package com.minh.product_service.query.queries;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FindCategoryBySlug {
    private String slug;
}
