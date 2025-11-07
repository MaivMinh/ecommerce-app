package com.minh.support_service.repository;

import com.minh.support_service.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {

    @Query(value = "SELECT * FROM cart_items CI WHERE CI.cart_id = :cartId AND CI.product_variant_id = :productVariantId", nativeQuery = true)
    Optional<CartItem> checkHasTheSameProductVariantInCartId(@Param("cartId") String cartId, @Param("productVariantId") String productVariantId);

    @Query(value = "SELECT CI.id as cartItemId, CI.product_variant_id as productVariantId FROM cart_items CI WHERE CI.cart_id = :cart_id", nativeQuery = true)
    List<CartItemProductVariantProjection> findAllProductVariantIdsByCartId(@Param("cart_id") String cart_id);

    @Query(value = "SELECT * FROM cart_items CI WHERE CI.id IN :cartItemIds", nativeQuery = true)
    List<CartItem> findAllByIds(@Param("cartItemIds") List<String> cartItemIds);
}
