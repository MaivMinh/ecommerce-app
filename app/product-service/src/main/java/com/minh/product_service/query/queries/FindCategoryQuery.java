package com.minh.product_service.query.queries;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FindCategoryQuery {
    private String id;
}
