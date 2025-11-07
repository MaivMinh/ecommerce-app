package com.minh.common.events;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentProcessedRollbackEvent {
    private String paymentId;
    private String orderId;
    private String reserveProductId;
    private String orderPromotionId;
    private String errorMsg;
}
