package com.minh.promotion_service.service.impl;

import com.minh.common.constants.ErrorCode;
import com.minh.common.events.PromotionAppliedEvent;
import com.minh.common.events.PromotionApplyRollbackedEvent;
import com.minh.common.message.MessageCommon;
import com.minh.common.utils.AppUtils;
import com.minh.promotion_service.dto.OrderPromotionDto;
import com.minh.promotion_service.dto.PromotionDTO;
import com.minh.promotion_service.entity.OrderPromotion;
import com.minh.promotion_service.repository.OrderPromotionRepository;
import com.minh.promotion_service.service.OrderPromotionService;
import com.minh.promotion_service.service.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderPromotionServiceImpl implements OrderPromotionService {
    private final OrderPromotionRepository repository;
    private final MessageCommon messageCommon;
    private final PromotionService promotionService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void applyPromotion(PromotionAppliedEvent event) {
        if (Objects.isNull(event.getPromotionId()))    {
            log.info("No promotion applied for this order: {}", event.getOrderId());
            return;
        }

        /// Find promotion.
        PromotionDTO promotion = promotionService.findById(event.getPromotionId());
        if (Objects.isNull(promotion)) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.Promotion.NOT_FOUND, event.getPromotionId()));
        }

        /// Check current quantity of this promotion.
        if (Objects.isNull(promotion.getUsageCount()) || promotion.getUsageCount() <= 0) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.Promotion.QUANTITY_USAGE_LIMITED, event.getPromotionId()));
        }

        OrderPromotion entity = new OrderPromotion();
        entity.setId(event.getOrderPromotionId());
        entity.setOrderId(event.getOrderId());
        entity.setPromotionId(promotion.getId());
        entity.setIsApplied(Boolean.TRUE);
        entity.setAppliedAt(new Timestamp(System.currentTimeMillis()));
        repository.save(entity);

        /// Decrease usage count of this promotion.
        promotion.setUsageCount(promotion.getUsageCount() - 1);
        promotionService.updatePromotion(promotion);
    }

    @Override
    @Transactional
    public void rollbackAppliedPromotion(PromotionApplyRollbackedEvent event) {
        OrderPromotion orderPromotion = repository.findById((event.getOrderPromotionId())).orElseThrow(
                () -> new RuntimeException("Không tìm thấy khuyến mãi đơn hàng")
        );
        String promotionId = orderPromotion.getPromotionId();
        PromotionDTO promotionDTO = promotionService.findById(promotionId);
        if (Objects.isNull(promotionDTO)) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.Promotion.NOT_FOUND, promotionId));
        }
        promotionDTO.setUsageCount(promotionDTO.getUsageCount() + 1);
        promotionService.updatePromotion(promotionDTO);

        /// Update all order promotions status to false.
        repository.updateIsAppliedByIds(Boolean.FALSE, List.of(orderPromotion.getId()));
    }
}
