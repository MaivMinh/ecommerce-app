package com.minh.common.events;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCompletionFailedEvent extends SagaEvent{
    private String orderId;
    private String username;
    private String paymentMethod;
    private String paymentId;
    private String errorMsg;
}