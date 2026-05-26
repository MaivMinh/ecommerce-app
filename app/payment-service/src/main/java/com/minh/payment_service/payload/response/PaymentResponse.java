package com.minh.payment_service.payload.response;

import com.minh.payment_service.enums.PaymentStatus;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private String paymentId;
    private String transactionId;
    private Integer status;
    private String message;
    private PaymentStatus paymentStatus;
    private String redirectUrl;
    private String providerOrderId;
}
