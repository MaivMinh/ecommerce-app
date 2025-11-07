package com.minh.promotion_service.query.projection;

import com.minh.promotion_service.command.events.PromotionCreatedEvent;
import com.minh.promotion_service.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup(value = "promotion-group")
public class PromotionProjection {
    private final PromotionService promotionService;

    @EventHandler
    public void on(PromotionCreatedEvent event) {
        promotionService.createPromotion(event);
    }
}
