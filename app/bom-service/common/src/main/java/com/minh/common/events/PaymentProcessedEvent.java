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
    private String orderPromotionId;
    private String reserveProductId;
    private Double total;
    private String currency;
    private String paymentMethodId;
    private String username;
    private String productId;
    private String errorMsg;
}
