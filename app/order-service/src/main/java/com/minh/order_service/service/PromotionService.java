package com.minh.order_service.service;


import com.minh.common.response.ResponseData;
import com.minh.order_service.DTOs.PromotionDTO;
import com.minh.order_service.payload.request.GetPromotionsRequest;
import com.minh.order_service.payload.request.CreatePromotionRequest;
import com.minh.order_service.payload.request.SearchPromotionsRequest;

public interface PromotionService {
    void createPromotion(CreatePromotionRequest event);

    ResponseData getPromotions(GetPromotionsRequest query);

    ResponseData searchPromotions(SearchPromotionsRequest query);

    PromotionDTO findById(String promotionId);

    void updatePromotion(PromotionDTO promotion);
}
