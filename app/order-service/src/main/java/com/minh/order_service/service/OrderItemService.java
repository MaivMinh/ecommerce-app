package com.minh.order_service.service;

import com.minh.order_service.query.entity.OrderItem;

import java.util.List;

public interface OrderItemService {
    void saveAll(List<OrderItem> items);

    void removeAllByOrderId(String orderId);

    List<OrderItem> getAllByOrderId(String id);
}
