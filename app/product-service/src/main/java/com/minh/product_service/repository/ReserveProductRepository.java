package com.minh.product_service.repository;

import com.minh.product_service.entity.ReserveProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReserveProductRepository extends JpaRepository<ReserveProduct, String> {
    @Query(value = "select * from reserve_products rp where rp.order_id = :orderId ", nativeQuery = true)
    List<ReserveProduct> findAllByOrderId(@Param("orderId") String orderId);

    @Modifying
    @Query(value = "update reserve_products rp set rp.status = 'completed' where rp.order_id = :orderId", nativeQuery = true)
    void updateAllItemsStatusToCompleted(@Param("orderId") String orderId);
}
