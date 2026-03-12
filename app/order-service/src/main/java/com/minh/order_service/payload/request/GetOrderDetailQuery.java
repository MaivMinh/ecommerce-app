package com.minh.order_service.payload.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetOrderDetailQuery {
    private String orderId;
}
