package com.minh.payment_service.command.events;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodDeletedEvent {
    private String id;
}
