package com.minh.payment_service.payload.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PayPalRedirectPayload {
    private String orderId;
    private String paymentId;
    private String paypalOrderId;
    private String redirectUrl;
    private String status;
}
