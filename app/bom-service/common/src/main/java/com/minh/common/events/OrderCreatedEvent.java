package com.minh.common.events;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreatedEvent extends SagaEvent {
    private String orderId;
    private String username;
    private String currency;
    private Double total;
    private String paymentMethod;
    private String shippingAddressId;
    private List<OrderItemCreatedEvent> orderItemDtos;
    private String productId;
}
