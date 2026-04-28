package com.minh.order_service.service;

import com.minh.order_service.entity.OrderItem;

import java.util.List;

public interface OrderItemService {
    void saveAll(List<OrderItem> items);

    List<OrderItem> getAllByOrderId(String id);
}
