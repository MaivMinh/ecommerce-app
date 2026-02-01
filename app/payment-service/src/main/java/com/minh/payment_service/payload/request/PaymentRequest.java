package com.minh.payment_service.payload.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest {
    private String paymentId;
    private String orderId;
    private String orderPromotionId;
    private String reserveProductId;
    private Double total;
    private String currency;
    private String paymentMethodId;
    private String username;
    private String productId;
    private String errorMsg;
}
