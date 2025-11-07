package com.minh.order_service.command.commands;

import com.minh.common.DTOs.OrderItemCreateDto;
import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
