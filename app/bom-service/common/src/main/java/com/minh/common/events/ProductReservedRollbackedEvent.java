package com.minh.common.events;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductReservedRollbackedEvent {
    private String reserveProductId;
    private String orderId;
    private String errorMsg;
}
