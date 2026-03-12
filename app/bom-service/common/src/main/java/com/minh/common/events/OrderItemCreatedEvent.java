package com.minh.common.events;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemCreatedEvent {
    private String id;
    private String productVariantId;
    private Integer quantity;
}