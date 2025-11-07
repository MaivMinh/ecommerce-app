package com.minh.product_service.query.projection;

import com.minh.product_service.command.events.CategoryCreatedEvent;
import com.minh.product_service.command.events.CategoryDeletedEvent;
import com.minh.product_service.command.events.CategoryUpdatedEvent;
import com.minh.product_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup("product-group")
public class CategoryProjection {
    private final CategoryService categoryService;

    @EventHandler
    public void on(CategoryCreatedEvent event) {
        categoryService.createCategory(event);
    }

    @EventHandler
    public void on(CategoryUpdatedEvent event) {
        categoryService.updateCategory(event);
    }

    @EventHandler
    public void on(CategoryDeletedEvent event) {
        categoryService.deleteCategory(event);
    }
}
