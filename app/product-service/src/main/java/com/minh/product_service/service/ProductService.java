package com.minh.product_service.service;

import com.minh.common.response.ResponseData;
import com.minh.product_service.dto.ProductDTO;
import com.minh.product_service.payload.request.*;
import product_service.*;

public interface ProductService {
    ResponseData findProducts(FindProductsQuery query);

    ResponseData findProductById(FindProductByIdQuery query);

    ResponseData findProductBySlug(FindProductBySlugQuery query);

    void createProduct(ProductDTO dto);

    void updateProduct(ProductDTO dto);

    void deleteProduct(String productId);

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
