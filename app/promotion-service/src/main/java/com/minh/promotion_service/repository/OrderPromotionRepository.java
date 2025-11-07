package com.minh.promotion_service.repository;

import com.minh.promotion_service.entity.OrderPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderPromotionRepository extends JpaRepository<OrderPromotion, String> {
    List<OrderPromotion> findAllByOrderId(String orderId);

    @Modifying
    @Query(value = "update order_promotions set is_applied  = :value where id IN :orderPromotionIds", nativeQuery = true)
    void updateIsAppliedByIds(@Param("value") Boolean value, @Param("orderPromotionIds") List<String> orderPromotionIds);
}
