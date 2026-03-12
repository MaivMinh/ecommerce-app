package com.minh.order_service.service.impl;

import com.minh.order_service.entity.OrderSagaState;
import com.minh.order_service.repository.OrderSagaStateRepository;
import com.minh.order_service.service.OrderSagaStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderSagaStateServiceImpl implements OrderSagaStateService {


    private final OrderSagaStateRepository orderSagaStateRepository;

    @Override
    public OrderSagaState findBySagaId(String sagaId) {
        if (!StringUtils.hasText(sagaId)) {
            throw new RuntimeException("Saga ID must not be null or empty");
        }
        return orderSagaStateRepository.findById(sagaId).orElseThrow(
                () -> new RuntimeException("Saga state not found for sagaId: " + sagaId)
        );
    }

    @Override
    @Transactional
    public void save(OrderSagaState state) {
        orderSagaStateRepository.save(state);
    }
}
