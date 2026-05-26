package com.minh.payment_service.repository;

import com.minh.payment_service.entity.Payment;
import com.minh.payment_service.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Payment findByOrderId(String orderId);
    Payment findByProviderOrderId(String providerOrderId);
    List<Payment> findAllByUsernameAndStatusAndApprovalUrlIsNotNull(String username, PaymentStatus status);
}
