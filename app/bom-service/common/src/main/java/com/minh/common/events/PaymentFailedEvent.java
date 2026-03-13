package com.minh.common.events;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentFailedEvent extends SagaEvent {
    private String orderId;
    private String username;
    private String errorMsg;
}
