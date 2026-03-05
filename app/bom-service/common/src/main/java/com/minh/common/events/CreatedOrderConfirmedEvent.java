package com.minh.common.events;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatedOrderConfirmedEvent {
    private String orderId;
    private String paymentId;
    private String reserveProductId;
    private String username;
    private String productId;
}
