package com.minh.order_service.service.impl;

import com.minh.order_service.entity.OrderItem;
import com.minh.order_service.repository.OrderItemRepository;
import com.minh.order_service.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    @Override
    @Transactional
    public void saveAll(List<OrderItem> items) {
        orderItemRepository.saveAll(items);
    }

    @Override
    public List<OrderItem> getAllByOrderId(String id) {
        return orderItemRepository.findAllByOrderId(id);
    }
}
