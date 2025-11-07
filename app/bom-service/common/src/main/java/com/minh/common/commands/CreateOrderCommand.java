package com.minh.common.commands;

import com.minh.common.DTOs.OrderItemCreateDto;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.List;

public class CreateOrderCommand {
    @TargetAggregateIdentifier
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
