package com.minh.promotion_service.query.handler;

import com.minh.promotion_service.payload.response.ResponseData;
import com.minh.promotion_service.query.queries.GetPromotionsQuery;
import com.minh.promotion_service.query.queries.SearchPromotionsQuery;
import com.minh.promotion_service.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromotionHandler {
    private final PromotionService promotionService;

    @QueryHandler
    public ResponseData handle(GetPromotionsQuery query) {
        return promotionService.getPromotions(query);
    }

    @QueryHandler
    public ResponseData handle(SearchPromotionsQuery query) {
        return promotionService.searchPromotions(query);
    }
}
