package com.minh.support_service.repository;

import com.minh.support_service.entity.PurchasedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchasedProductRepository extends JpaRepository<PurchasedProduct, String> {
    PurchasedProduct findPurchasedProductByProductIdAndUsername(String productId, String username);
}
