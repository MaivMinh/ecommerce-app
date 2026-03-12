package com.minh.product_service.service;

import com.minh.common.exception.ResourceNotFoundException;
import com.minh.common.response.ResponseData;
import com.minh.product_service.dto.CategoryDTO;
import com.minh.product_service.payload.request.FindAllCategoriesQuery;
import com.minh.product_service.payload.request.FindCategoriesQuery;
import com.minh.product_service.payload.request.FindCategoryBySlug;
import com.minh.product_service.payload.request.SearchCategoriesByNameQuery;

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

    void createCategory(CategoryDTO dto);
    void updateCategory(CategoryDTO categoryDTO);
    void deleteCategory(String categoryId);

    ResponseData findCategories(FindCategoriesQuery query);
}
