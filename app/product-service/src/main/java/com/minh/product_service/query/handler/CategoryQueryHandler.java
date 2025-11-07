package com.minh.product_service.query.handler;

import com.minh.common.exception.ResourceNotFoundException;
import com.minh.common.message.MessageCommon;
import com.minh.common.response.ResponseData;
import com.minh.product_service.query.queries.*;
import com.minh.product_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CategoryQueryHandler {
    private final CategoryService categoryService;
    private final MessageCommon messageCommon;

    @QueryHandler
    public ResponseData handle(FindCategoryByIdQuery findCategoryByIdQuery) throws ResourceNotFoundException {
        String categoryId = findCategoryByIdQuery.getCategoryId();
        return categoryService.getCategoryById(categoryId);
    }

    @QueryHandler
    public ResponseData handle(FindAllCategoriesQuery query) {
        return categoryService.findAllCategories(query);
    }

    @QueryHandler
    public ResponseData handle(FindCategoryBySlug query) {
        return categoryService.findCategoryBySlug(query);
    }

    @QueryHandler
    public ResponseData handle(SearchCategoriesByNameQuery query) {
        return categoryService.searchCategoriesByName(query);
    }

    @QueryHandler
    public ResponseData handle(FindCategoriesQuery query) {
        return categoryService.findCategories(query);
    }
}
