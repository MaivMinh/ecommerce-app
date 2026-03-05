package com.minh.order_service.service;


import com.minh.common.response.ResponseData;
import com.minh.order_service.DTOs.PromotionDTO;
import com.minh.order_service.command.events.PromotionCreatedEvent;
import com.minh.order_service.query.queries.GetPromotionsQuery;
import com.minh.order_service.query.queries.SearchPromotionsQuery;

public interface PromotionService {
    void createPromotion(PromotionCreatedEvent event);

    ResponseData getPromotions(GetPromotionsQuery query);

    ResponseData searchPromotions(SearchPromotionsQuery query);

    PromotionDTO findById(String promotionId);

    void updatePromotion(PromotionDTO promotion);
}
