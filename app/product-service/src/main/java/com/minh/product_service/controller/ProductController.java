package com.minh.product_service.controller;

import com.minh.common.enums.ProductStatus;
import com.minh.common.response.ResponseData;
import com.minh.product_service.dto.ProductDTO;
import com.minh.product_service.payload.request.*;
import com.minh.product_service.dto.SearchProductDTO;
import com.minh.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping(value = "/api/products")
@Validated
@RestController
public class ProductController {
    private final ProductService service;

    @PostMapping(value = "")
    public ResponseEntity<ResponseData> createProduct(@RequestBody @Valid ProductDTO dto) {
        service.createProduct(dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "")
    public ResponseEntity<ResponseData> updateProduct(@RequestBody @Valid ProductDTO dto) {
        service.updateProduct(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{productId}")
    public ResponseEntity<ResponseData> deleteProduct(@PathVariable(name = "productId") String productId) {
        service.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }


    @GetMapping(value = "/newest")
    public ResponseEntity<ResponseData> findNewestProducts(
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        page = (page > 0) ? (page - 1) : 0;
        size = (size > 0) ? size : 10;
        FindNewestProductsQuery query = FindNewestProductsQuery.builder()
                .page(page)
                .size(size)
                .build();

        ResponseData response = service.findNewestProducts(query);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(value = "/slug")
    public ResponseEntity<ResponseData> findProductBySlug(@RequestParam(value = "name") String slug) {
        FindProductBySlugQuery query = FindProductBySlugQuery.builder()
                .slug(slug)
                .build();

        ResponseData response = service.findProductBySlug(query);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(value = "/search-by-keyword")
    public ResponseEntity<ResponseData> searchProductsByKeyword(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        SearchProductByKeywordQuery query = SearchProductByKeywordQuery.builder()
                .keyword(keyword)
                .build();

        query.setPage(page);
        query.setSize(size);

        ResponseData response = service.searchProductByKeyword(query);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(value = "/find-by-product-variant")
    public ResponseEntity<ResponseData> findProductByProductVariantId(
            @RequestParam(name = "productVariantId") String productVariantId) {
        FindProductByProductVariantIdQuery query = FindProductByProductVariantIdQuery.builder()
                .productVariantId(productVariantId)
                .build();

        ResponseData response = service.findProductByProductVariantId(query);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(value = "/{productId}")
    public ResponseEntity<ResponseData> findProductById(@PathVariable(value = "productId") String productId) {
        FindProductByIdQuery query = FindProductByIdQuery.builder()
                .productId(productId)
                .build();

        ResponseData response = service.findProductById(query);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @GetMapping(value = "/{productId}/variants")
    public ResponseEntity<ResponseData> findProductVariantsByProductId(@PathVariable(value = "productId") String productId) {
        FindProductVariantsByProductIdQuery query = FindProductVariantsByProductIdQuery.builder()
                .productId(productId)
                .build();

        ResponseData response = service.findProductVariantsByProductId(query);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(value = "")
    public ResponseEntity<ResponseData> findProducts(@RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
                                                     @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {
        page = (page > 0) ? (page - 1) : 0;
        size = (size > 0) ? size : 10;
        FindProductsQuery query = FindProductsQuery.builder()
                .page(page)
                .size(size)
                .build();

        ResponseData response = service.findProducts(query);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(value = "/search")
    public ResponseEntity<ResponseData> searchProducts(
            @RequestBody SearchProductDTO searchProductDTO,
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "", required = false) String sort) {
        SearchProductQuery query = SearchProductQuery.builder()
                .page((page > 0) ? (page - 1) : 0)
                .size((size > 0) ? size : 10)
                .sort(sort)
                .categoryIds(searchProductDTO.getCategoryIds())
                .minPrice(searchProductDTO.getMinPrice())
                .maxPrice(searchProductDTO.getMaxPrice())
                .rating(searchProductDTO.getRating())
                .isNew(searchProductDTO.getIsNew())
                .isFeatured(searchProductDTO.getIsFeatured())
                .isBestseller(searchProductDTO.getIsBestseller())
                .status(searchProductDTO.getStatus() != null ? ProductStatus.valueOf(searchProductDTO.getStatus()) : null)
                .build();

        ResponseData response = service.searchProducts(query);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}