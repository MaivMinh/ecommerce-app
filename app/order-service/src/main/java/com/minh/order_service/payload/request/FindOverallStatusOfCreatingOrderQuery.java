package com.minh.order_service.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FindOverallStatusOfCreatingOrderQuery {
    private String orderId;
}
