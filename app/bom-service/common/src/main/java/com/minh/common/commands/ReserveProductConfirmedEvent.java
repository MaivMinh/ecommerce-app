package com.minh.common.commands;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReserveProductConfirmedEvent {
    private String reserveProductId;
    private String orderPromotionId;
    private String orderId;
    private String paymentId;
    private String username;
    private String productId;
    private String errorMsg;
}
