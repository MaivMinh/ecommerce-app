package com.minh.product_service.query.queries;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FindAllCategoriesQuery {
    private String type;
    private String page;
    private String size;
    private String sort;
}
