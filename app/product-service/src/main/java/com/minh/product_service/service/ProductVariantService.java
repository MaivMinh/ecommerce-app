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

    /**
     * Hàm thực hiện giảm số lượng sản phẩm của biến thể mà không gây ra lỗi overselling.
     * @param productVariantId: ID của biến thể sản phẩm cần giảm số lượng.
     * @param quantity: Số lượng cần giảm.
     */
    void decreaseProductVariantQuantity(String productVariantId, Integer quantity);
}
