package com.minh.order_service.repository;

import com.minh.order_service.entity.OrderSagaState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderSagaStateRepository extends JpaRepository<OrderSagaState, String> {

}
