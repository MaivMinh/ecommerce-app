package com.minh.common.events;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRefundedEvent extends SagaEvent {
    private String orderId;
    private String username;
    private String paymentMethod;
    private String errorMsg;
}
