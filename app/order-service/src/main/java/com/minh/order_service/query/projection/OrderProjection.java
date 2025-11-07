package com.minh.order_service.query.projection;

import com.minh.common.events.CreatedOrderConfirmedEvent;
import com.minh.common.events.OrderCreatedEvent;
import com.minh.common.events.OrderCreatedRollbackedEvent;
import com.minh.order_service.payload.response.ResponseData;
import com.minh.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ProcessingGroup("order-group")
public class OrderProjection {
    private final OrderService orderService;

    @EventHandler
    public void on(OrderCreatedEvent event) {
        orderService.createOrder(event);
    }
    @EventHandler
    public void on(OrderCreatedRollbackedEvent event) {
        orderService.rollbackOrderCreated(event);
    }
    @EventHandler
    public void on(CreatedOrderConfirmedEvent event) {
        orderService.confirmCreatedOrder(event);
    }
}
