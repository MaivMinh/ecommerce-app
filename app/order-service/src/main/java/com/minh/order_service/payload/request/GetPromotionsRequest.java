package com.minh.order_service.payload.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class GetPromotionsRequest {
    int page;
    int size;
}
