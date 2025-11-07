package com.minh.product_service.service;

import com.minh.product_service.dto.ProductVariantDTO;
import com.minh.product_service.payload.response.ProductVariantGrpc;

import java.util.List;

public interface ProductVariantService {
    void createProductVariant(ProductVariantDTO productVariantDTO);


    List<ProductVariantDTO> findProductVariantsByProductId(String id);

    void updateProductVariant(ProductVariantDTO productVariantDTO);

    void deleteProductVariant(String id);

    List<ProductVariantDTO> findProductVariantsByProductIds(List<String> productIds);

    List<ProductVariantDTO> findProductVariantsByIds(List<String> productVariantIds);

    ProductVariantDTO findById(String productVariantId);

    List<ProductVariantGrpc> findProductVariantsByIdsGrpc(List<String> productVariantIds);
}
