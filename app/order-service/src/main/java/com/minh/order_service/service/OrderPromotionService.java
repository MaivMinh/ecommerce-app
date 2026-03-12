package com.minh.order_service.service;

import com.minh.order_service.DTOs.OrderPromotionDto;

public interface OrderPromotionService {
    /**
     * Áp dụng khuyến mãi cho một đơn hàng cụ thể.
     * @param event: Sự kiện về hành động áp dụng khuyến mãi thành công cho một đơn hàng cụ thể.
     */
    void applyPromotion(OrderPromotionDto event);
}
