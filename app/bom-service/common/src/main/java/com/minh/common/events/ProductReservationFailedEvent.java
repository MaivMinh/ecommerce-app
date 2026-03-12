package com.minh.common.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductReservationFailedEvent extends SagaEvent{
    private String orderId;
    private String username;
    private String errorMsg;
}
