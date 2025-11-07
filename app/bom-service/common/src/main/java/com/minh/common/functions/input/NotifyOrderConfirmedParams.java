package com.minh.common.functions.input;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotifyOrderConfirmedParams {
    private Double total;
    private String orderId;
    private List<OrderedItem> items;
}
