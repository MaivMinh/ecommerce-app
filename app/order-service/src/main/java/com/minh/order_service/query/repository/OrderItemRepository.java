package com.minh.order_service.query.repository;

import com.minh.order_service.query.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    @Modifying
    @Query(value = "delete from order_items where order_id = :orderId", nativeQuery = true)
    void removeAllByOrderId(@Param("orderId") String orderId);

    @Query(value = "select * from order_items where order_id = :id", nativeQuery = true)
    List<OrderItem> findAllByOrderId(@Param("id") String id);
}
