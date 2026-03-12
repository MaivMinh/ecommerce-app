package com.minh.common.events;

import com.minh.common.enums.PaymentStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentProcessedEvent extends SagaEvent {
    private String orderId;
    private String paymentId;
    private Double total;
    private String currency;
    private String paymentMethod;
    private String username;
}