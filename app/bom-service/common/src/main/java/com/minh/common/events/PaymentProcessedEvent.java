package com.minh.common.events;

import lombok.*;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentProcessedEvent {
    private String paymentId;
    private String orderId;
    private String reserveProductId;
    private Double total;
    private String currency;
    private String paymentMethod;
    private String username;
    private String productId;
    private String errorMsg;
}
