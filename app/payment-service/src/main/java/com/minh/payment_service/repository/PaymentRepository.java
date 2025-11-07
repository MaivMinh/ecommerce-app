package com.minh.payment_service.repository;

import com.minh.payment_service.query.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    @Query(value = "select * from payments where order_id = :orderId ", nativeQuery = true)
    Payment findByOrOrderId(@Param("orderId") String orderId);

    Payment findByOrderId(String orderId);
}
