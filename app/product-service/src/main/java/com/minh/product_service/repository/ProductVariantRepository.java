package com.minh.product_service.repository;

import com.minh.product_service.entity.ProductVariant;
import com.minh.product_service.repository.projection.ProductVariantGrpcProjection;
import com.netflix.spectator.api.Registry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, String> {

    List<ProductVariant> findAllByProductId(String productId);

    @Query(value = "select * " +
            "from product_variants pv " +
            "where pv.product_id in :productIds", nativeQuery = true)
    List<ProductVariant> findAllByProductIdIn(@Param(value = "productIds") List<String> productIds);

    @Query(value = "SELECT * FROM product_variants PV WHERE PV.id IN :productVariantIds", nativeQuery = true)
    List<ProductVariant> findProductVariantsByIds(@Param("productVariantIds") List<String> productVariantIds);

    @Query(value = "SELECT pvs.id, ps.name, ps.slug, pvs.size, pvs.color_name as colorName, pvs.color_hex as colorHex, ps.cover, pvs.price, pvs.original_price as originalPrice\n" +
            "FROM product_variants pvs join products ps on pvs.product_id = ps.id\n" +
            "WHERE pvs.id IN (:#{#productVariantIds})"
            , nativeQuery = true)
    List<ProductVariantGrpcProjection> findProductVariantsByIdsGrpc(@Param("productVariantIds") List<String> productVariantIds);
}
