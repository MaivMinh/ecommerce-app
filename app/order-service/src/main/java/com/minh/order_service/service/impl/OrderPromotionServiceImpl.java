package com.minh.order_service.service.impl;

import com.minh.common.constants.ErrorCode;
import com.minh.common.message.MessageCommon;
import com.minh.order_service.DTOs.OrderPromotionDto;
import com.minh.order_service.DTOs.PromotionDTO;
import com.minh.order_service.entity.OrderPromotion;
import com.minh.order_service.repository.OrderPromotionRepository;
import com.minh.order_service.service.OrderPromotionService;
import com.minh.order_service.service.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderPromotionServiceImpl implements OrderPromotionService {
    private final OrderPromotionRepository repository;
    private final MessageCommon messageCommon;
    private final PromotionService promotionService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void applyPromotion(OrderPromotionDto request) {
        if (Objects.isNull(request.getPromotionId()))    {
            log.info("No promotion applied for this order: {}", request.getOrderId());
            return;
        }

        /// Find promotion.
        PromotionDTO promotion = promotionService.findById(request.getPromotionId());
        if (Objects.isNull(promotion)) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.Promotion.NOT_FOUND, request.getPromotionId()));
        }

        /// Check current quantity of this promotion.
        if (Objects.isNull(promotion.getUsageCount()) || promotion.getUsageCount() <= 0) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.Promotion.QUANTITY_USAGE_LIMITED, request.getPromotionId()));
        }

        OrderPromotion entity = new OrderPromotion();
        modelMapper.map(request,entity);
        repository.save(entity);
    }
}
