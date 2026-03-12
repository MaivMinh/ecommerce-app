package com.minh.common.events;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservedProductConfirmedEvent extends SagaEvent {
    private String orderId;
}
