package com.minh.support_service.service;

import com.minh.support_service.DTO.PurchasedProductDto;

public interface PurchasedProductService {
    void savePurchasedOrder(PurchasedProductDto dto);

    PurchasedProductDto findPurchasedProductByProductIdAndUsername(String username, String productId);
}
