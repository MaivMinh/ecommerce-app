package com.minh.promotion_service.service;

import com.minh.promotion_service.command.events.PromotionCreatedEvent;
import com.minh.promotion_service.dto.PromotionDTO;
import com.minh.promotion_service.payload.response.ResponseData;
import com.minh.promotion_service.query.queries.GetPromotionsQuery;
import com.minh.promotion_service.query.queries.SearchPromotionsQuery;

public interface PromotionService {
    void createPromotion(PromotionCreatedEvent event);

    ResponseData getPromotions(GetPromotionsQuery query);

    ResponseData searchPromotions(SearchPromotionsQuery query);

    PromotionDTO findById(String promotionId);

    void updatePromotion(PromotionDTO promotion);
}
