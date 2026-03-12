package com.minh.product_service.payload.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FindCategoryQuery {
    private String id;
}
