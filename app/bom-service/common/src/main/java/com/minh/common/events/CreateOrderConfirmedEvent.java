package com.minh.common.events;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderConfirmedEvent {
    private String orderId;
    private String paymentId;
    private String reserveProductId;
    private String orderPromotionId;
    private String username;
    private String productId;
}
