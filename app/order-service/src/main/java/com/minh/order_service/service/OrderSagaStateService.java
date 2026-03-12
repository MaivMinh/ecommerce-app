package com.minh.order_service.service;

import com.minh.order_service.entity.OrderSagaState;

public interface OrderSagaStateService {
    OrderSagaState findBySagaId(String sagaId);

    void save(OrderSagaState state);
}
