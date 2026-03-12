package com.minh.common.events;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCancelledEvent extends SagaEvent {
    private String orderId;
    private String errorMsg;
    private String username;
}