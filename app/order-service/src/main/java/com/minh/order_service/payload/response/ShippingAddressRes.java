package com.minh.order_service.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingAddressRes {
    private String id;
    private String fullName;
    private String phone;
    private String address;
}