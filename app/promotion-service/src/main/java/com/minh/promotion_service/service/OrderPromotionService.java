package com.minh.promotion_service.service;

import com.minh.common.events.PromotionAppliedEvent;
import com.minh.common.events.PromotionApplyRollbackedEvent;
import com.minh.promotion_service.dto.OrderPromotionDto;

public interface OrderPromotionService {
    /**
     * Áp dụng khuyến mãi cho một đơn hàng cụ thể.
     * @param event: Sự kiện về hành động áp dụng khuyến mãi thành công cho một đơn hàng cụ thể.
     */
    void applyPromotion(PromotionAppliedEvent event);

    void rollbackAppliedPromotion(PromotionApplyRollbackedEvent event);
}
