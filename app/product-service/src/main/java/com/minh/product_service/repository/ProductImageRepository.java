package com.minh.product_service.repository;

import com.minh.product_service.entity.ProductImage;
import com.netflix.spectator.api.Registry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, String> {

    List<ProductImage> findAllByProductId(String productId);

    @Query(value = "select *" +
            "from product_images pi " +
            "where pi.product_id in :productIds", nativeQuery = true)
    List<ProductImage> findProductImagesByProductIdIn(@Param(value = "productIds") List<String> productIds);

    @Query(value = "SELECT * FROM product_images PI WHERE PI.product_id IN :productIds", nativeQuery = true)
    List<ProductImage> findProductImagesByProductIds(@Param("productIds") List<String> productIds);
}
