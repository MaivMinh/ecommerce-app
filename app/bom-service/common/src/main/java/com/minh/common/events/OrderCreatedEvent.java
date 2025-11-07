package com.minh.common.events;

import com.minh.common.DTOs.OrderItemCreateDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class OrderCreatedEvent {
    private String orderId;
    private String username;
    private String shippingAddressId;
    private Double subTotal;
    private Double discount;
    private Double total;
    private String paymentMethodId;
    private String promotionId;
    private String currency;
    private String note;
    private List<OrderItemCreateDto> orderItemDtos;
    private String productId;
}
