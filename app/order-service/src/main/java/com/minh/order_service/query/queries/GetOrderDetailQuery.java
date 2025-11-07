package com.minh.order_service.query.queries;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetOrderDetailQuery {
    private String orderId;
}
