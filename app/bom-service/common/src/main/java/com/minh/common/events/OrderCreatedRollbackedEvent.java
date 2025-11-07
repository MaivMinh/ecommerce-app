package com.minh.common.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCreatedRollbackedEvent {
    private String orderId;
    private String errorMsg;
}
