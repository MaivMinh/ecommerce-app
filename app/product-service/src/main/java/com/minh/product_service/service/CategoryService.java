package com.minh.product_service.service;

import com.minh.common.exception.ResourceNotFoundException;
import com.minh.common.response.ResponseData;
import com.minh.product_service.command.events.CategoryCreatedEvent;
import com.minh.product_service.command.events.CategoryDeletedEvent;
import com.minh.product_service.command.events.CategoryUpdatedEvent;
import com.minh.product_service.query.queries.FindAllCategoriesQuery;
import com.minh.product_service.query.queries.FindCategoriesQuery;
import com.minh.product_service.query.queries.FindCategoryBySlug;
import com.minh.product_service.query.queries.SearchCategoriesByNameQuery;

public interface CategoryService {
    /**
     * Retrieves a category by its ID.
     *
     * @param categoryId the ID of the category to retrieve
     * @return the category with the specified ID
     */
    ResponseData getCategoryById(String categoryId) throws ResourceNotFoundException;

    /**
     * Retrieves all categories.
     *
     * @return a list of all categories
     */
    ResponseData findAllCategories(FindAllCategoriesQuery query);

    ResponseData findCategoryBySlug(FindCategoryBySlug query);

    ResponseData searchCategoriesByName(SearchCategoriesByNameQuery query);

    void createCategory(CategoryCreatedEvent event);
    void updateCategory(CategoryUpdatedEvent event);
    void deleteCategory(CategoryDeletedEvent event);

    ResponseData findCategories(FindCategoriesQuery query);
}
