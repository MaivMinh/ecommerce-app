package com.minh.product_service.command.events;

import lombok.Data;

@Data
public class ProductDeletedEvent {
    private String id;
}
