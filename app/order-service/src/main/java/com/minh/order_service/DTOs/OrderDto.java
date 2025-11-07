package com.minh.order_service.DTOs;

import com.minh.order_service.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class OrderDto {
    private String id;
    private Double subTotal;
    private Double discount;
    private Double total;
    private String currency;
    private String note;
    private OrderStatus status;
    private Timestamp createdAt;
    private String createdBy;
}
