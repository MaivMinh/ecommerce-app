package com.minh.common.events;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionApplyRollbackedEvent {
    private String orderPromotionId;
    private String reserveProductId;
    private String orderId;
    private String errorMsg;
}
