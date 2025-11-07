package com.minh.product_service.query.handler;

import com.minh.common.response.ResponseData;
import com.minh.product_service.query.queries.*;
import com.minh.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductQueryHandler {
    private final ProductService productService;

    @QueryHandler
    public ResponseData handle(FindProductByIdQuery query) {
        return productService.findProductById(query);
    }

    @QueryHandler
    public ResponseData handle(FindProductBySlugQuery query) {
        return productService.findProductBySlug(query);
    }

    @QueryHandler
    public ResponseData handle(FindProductsQuery query) {
        return productService.findProducts(query);
    }

    @QueryHandler
    public ResponseData handle(SearchProductQuery query) {
        return productService.searchProducts(query);
    }

    @QueryHandler
    public ResponseData handle(FindProductVariantsByProductIdQuery query) {
        return productService.findProductVariantsByProductId(query);
    }

    @QueryHandler
    public ResponseData handle(FindNewestProductsQuery query) {
        return productService.findNewestProducts(query);
    }

    @QueryHandler
    public ResponseData handle(SearchProductByKeywordQuery query) {
        return productService.searchProductByKeyword(query);
    }

    @QueryHandler
    public ResponseData handle(FindProductByProductVariantIdQuery query) {
        return productService.findProductByProductVariantId(query);
    }
}