package com.minh.product_service.controller;

import com.minh.common.response.ResponseData;
import com.minh.product_service.dto.CategoryDTO;
import com.minh.product_service.payload.request.*;
import com.minh.product_service.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/categories")
@Validated
public class CategoryController {
    private final CategoryService service;

    @PostMapping(value = "")
    public ResponseEntity<ResponseData> createCategory(@RequestBody @Valid CategoryDTO categoryDTO) {
        service.createCategory(categoryDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "")
    public ResponseEntity<ResponseData> updateCategory(@RequestBody @Valid CategoryDTO categoryDTO) {
        service.updateCategory(categoryDTO);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping(value = "/{categoryId}")
    public ResponseEntity<ResponseData> deleteCategory(@PathVariable(name = "categoryId") String categoryId) {
        service.deleteCategory(categoryId);
        return ResponseEntity.ok().build();
    }


    @GetMapping(value = "/{categoryId}", produces = "application/json")
    public ResponseEntity<ResponseData> getCategoryById(@PathVariable(name = "categoryId") String categoryId) {
        ResponseData response = service.getCategoryById(categoryId);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @GetMapping(value = "/slug", produces = "application/json")
    public ResponseEntity<ResponseData> findCategoryBySlug(@RequestParam(value = "name") String name) {
        FindCategoryBySlug query = FindCategoryBySlug.builder()
                .slug(name)
                .build();

        ResponseData response = service.findCategoryBySlug(query);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<ResponseData> findAllCategories() {
        FindAllCategoriesQuery query = FindAllCategoriesQuery.builder()
                .build();

        ResponseData response = service.findAllCategories(query);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(value = "")
    public ResponseEntity<ResponseData> findAllCategories(@RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
                                                          @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {

        page = (page > 0) ? (page - 1) : 0;
        size = (size > 0) ? size : 10;

        FindCategoriesQuery query = FindCategoriesQuery.builder()
                .page(page)
                .size(size)
                .build();

        ResponseData response = service.findCategories(query);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<ResponseData> searchCategoriesByName(@RequestParam(name = "name") String name) {
        SearchCategoriesByNameQuery query = SearchCategoriesByNameQuery.builder()
                .name(name)
                .build();

        ResponseData response = service.searchCategoriesByName(query);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }
}
