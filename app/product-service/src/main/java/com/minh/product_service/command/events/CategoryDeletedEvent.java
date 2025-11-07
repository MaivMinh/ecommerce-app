package com.minh.product_service.command.events;

import lombok.Data;

@Data
public class CategoryDeletedEvent {
    private String id;
}
