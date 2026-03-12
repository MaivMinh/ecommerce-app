package com.minh.common.commands;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefundPaymentCommand extends SagaCommand {
    private String orderId;
    private String paymentId;
    private String username;
    private String errorMsg;
}