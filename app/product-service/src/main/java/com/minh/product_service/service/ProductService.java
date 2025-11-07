package com.minh.product_service.service;

import com.minh.common.response.ResponseData;
import com.minh.product_service.command.events.ProductCreatedEvent;
import com.minh.product_service.command.events.ProductDeletedEvent;
import com.minh.product_service.command.events.ProductUpdatedEvent;
import com.minh.product_service.query.queries.*;
import product_service.*;

public interface ProductService {
    ResponseData findProducts(FindProductsQuery query);

    ResponseData findProductById(FindProductByIdQuery query);

    ResponseData findProductBySlug(FindProductBySlugQuery query);

    void createProduct(ProductCreatedEvent event);

    void updateProduct(ProductUpdatedEvent event);

    void deleteProduct(ProductDeletedEvent event);

    ResponseData searchProducts(SearchProductQuery query);

    ResponseData findProductVariantsByProductId(FindProductVariantsByProductIdQuery query);

    ResponseData findNewestProducts(FindNewestProductsQuery query);

    FindProductVariantByIdResponse findProductVariantById(FindProductVariantByIdRequest request);

    FindProductVariantsByIdsResponse findProductVariantsByIds(FindProductVariantsByIdsRequest request);

    FindProductVariantByListProductVariantIdResponse findProductVariantByListId(FindProductVariantByListProductVariantIdRequest request);

    ResponseData searchProductByKeyword(SearchProductByKeywordQuery query);

    ResponseData findProductByProductVariantId(FindProductByProductVariantIdQuery query);

    FindProductInfoByProductVariantIdResponse findProductInfoByProductVariantId(FindProductInfoByProductVariantIdRequest request);
}
