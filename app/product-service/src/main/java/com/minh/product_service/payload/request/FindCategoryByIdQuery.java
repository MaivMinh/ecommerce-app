package com.minh.product_service.payload.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FindCategoryByIdQuery {
    private String categoryId;
}
