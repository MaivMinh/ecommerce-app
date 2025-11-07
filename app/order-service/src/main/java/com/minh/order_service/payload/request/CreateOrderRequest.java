package com.minh.order_service.payload.request;

import com.minh.common.DTOs.OrderItemCreateDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {
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
