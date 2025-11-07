package com.minh.order_service.payload.response;

import com.minh.order_service.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class OrderDetailRes {
    private String id;
    private ShippingAddressRes shippingAddress;
    private Double subTotal;
    private Double discount;
    private Double total;
    private String currency;
    private String note;
    private String status;
    private List<OrderItemRes> items;
    private Timestamp createdAt;
    private String createdBy;
    private PaymentStatus paymentStatus;
}