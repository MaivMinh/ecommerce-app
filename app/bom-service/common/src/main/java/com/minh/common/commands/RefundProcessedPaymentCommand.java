package com.minh.common.commands;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefundProcessedPaymentCommand extends SagaCommand {
    private String orderId;
    private String paymentId;
    private String paymentMethod;
    private String username;
}
