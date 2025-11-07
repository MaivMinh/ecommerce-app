package com.minh.common.events;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionAppliedEvent {
    private String orderPromotionId;
    private String reserveProductId;
    private String orderId;
    private String promotionId;
    private String paymentMethodId;
    private Double total;
    private String currency;
    private String username;
    private String productId;
    private String errorMsg;
}
