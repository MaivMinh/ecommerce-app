package com.minh.common.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductReservedEvent extends SagaEvent {
    private String orderId;
    private String paymentMethod;
    private Double total;
    private String currency;
    private String username;
}
