package com.minh.common.commands;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessPaymentCommand extends SagaCommand {
    private String orderId;
    private String paymentId;
    private Double total;
    private String currency;
    private String paymentMethod;
    private String username;
    private String productId;
}
