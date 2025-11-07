package com.minh.product_service.query.controller;

import com.minh.common.response.ResponseData;
import com.minh.product_service.query.queries.*;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(value = "/api/categories")
public class CategoryQueryController {
    private final QueryGateway queryGateway;

    @GetMapping(value = "/{categoryId}", produces = "application/json")
    public ResponseEntity<ResponseData> getCategoryById(@PathVariable(name = "categoryId") String categoryId) {
        FindCategoryByIdQuery query = FindCategoryByIdQuery.builder()
                .categoryId(categoryId)
                .build();

        ResponseData response = queryGateway.query(query, ResponseTypes.instanceOf(ResponseData.class)).join();
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @GetMapping(value = "/slug", produces = "application/json")
    public ResponseEntity<ResponseData> findCategoryBySlug(@RequestParam(value = "name") String name) {
        FindCategoryBySlug query = FindCategoryBySlug.builder()
                .slug(name)
                .build();

        ResponseData response = queryGateway.query(query, ResponseTypes.instanceOf(ResponseData.class)).join();
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<ResponseData> findAllCategories() {
        FindAllCategoriesQuery query = FindAllCategoriesQuery.builder()
                .build();

        ResponseData response = queryGateway.query(query, ResponseTypes.instanceOf(ResponseData.class)).join();
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

        ResponseData response = queryGateway.query(query, ResponseTypes.instanceOf(ResponseData.class)).join();
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<ResponseData> searchCategoriesByName(@RequestParam(name = "name") String name) {
        SearchCategoriesByNameQuery query = SearchCategoriesByNameQuery.builder()
                .name(name)
                .build();

        ResponseData response = queryGateway.query(query, ResponseTypes.instanceOf(ResponseData.class)).join();
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }
}
