package com.minh.product_service.query.queries;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FindCategoryByIdQuery {
    private String categoryId;
}
