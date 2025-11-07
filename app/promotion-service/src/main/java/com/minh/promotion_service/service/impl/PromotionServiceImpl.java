package com.minh.promotion_service.service.impl;

import com.minh.common.constants.ErrorCode;
import com.minh.common.constants.ResponseMessages;
import com.minh.common.events.PromotionAppliedEvent;
import com.minh.common.message.MessageCommon;
import com.minh.common.utils.AppUtils;
import com.minh.promotion_service.command.events.PromotionCreatedEvent;
import com.minh.promotion_service.dto.PromotionDTO;
import com.minh.promotion_service.entity.OrderPromotion;
import com.minh.promotion_service.entity.Promotion;
import com.minh.promotion_service.payload.response.ResponseData;
import com.minh.promotion_service.query.queries.GetPromotionsQuery;
import com.minh.promotion_service.query.queries.SearchPromotionsQuery;
import com.minh.promotion_service.repository.PromotionRepository;
import com.minh.promotion_service.service.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final MessageCommon messageCommon;
    private final ModelMapper modelMapper;

    private Map<String, Object> toPayload(Page<Promotion> promotions) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("totalElements", promotions.getTotalElements());
        payload.put("totalPages", promotions.getTotalPages());
        payload.put("page", promotions.getNumber() + 1);
        payload.put("size", promotions.getSize());
        payload.put("promotions", promotions.getContent().stream().map(promotion -> modelMapper.map(promotion, PromotionDTO.class)).collect(Collectors.toList()));
        return payload;
    }

    @Override
    public void createPromotion(PromotionCreatedEvent event) {
        if (!StringUtils.hasText(event.getPromotionId())) throw new IllegalArgumentException(messageCommon.getMessage(
                ErrorCode.INVALID_PARAMS, "promotionId"
        ));
        /**
         * Nên sử dụng ModelMapper để map thay vì BeanUtils vì BeanUtils sẽ gán các giá trị null cho các trường không có trong event.
         * Vì vậy, các trường như createdAt, createdBy, ... sẽ bị gán null, dẫn đến lỗi khi lưu vào database.
         */
        Promotion promotion = modelMapper.map(event, Promotion.class);
        promotion.setId(event.getPromotionId());
        promotionRepository.save(promotion);
    }

    @Override
    public ResponseData getPromotions(GetPromotionsQuery query) {
        int page = Math.max(0, query.getPage());
        int size = Math.max(1, query.getSize());

        Pageable pageable = PageRequest.of(page, size);
        Page<Promotion> promotions = promotionRepository.findAll(pageable);

        Map<String, Object> payload = this.toPayload(promotions);

        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(payload)
                .build();
    }

    @Override
    public ResponseData searchPromotions(SearchPromotionsQuery query) {
        Pageable pageable = AppUtils.toPageable(query);
        Page<Promotion> promotions = promotionRepository.searchPromotions(query, pageable);

        Map<String, Object> payload = this.toPayload(promotions);

        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(payload)
                .build();
    }

    @Override
    public PromotionDTO findById(String promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId).orElse(null);
        if (Objects.isNull(promotion)) return null;
        return modelMapper.map(promotion, PromotionDTO.class);
    }

    @Override
    public void updatePromotion(PromotionDTO dto) {
        Promotion promotion = promotionRepository.findById(dto.getId()).orElse(null);
        if (Objects.isNull(promotion)) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.Promotion.NOT_FOUND , dto.getId()));
        }
        modelMapper.map(dto,promotion);
        promotionRepository.save(promotion);
    }
}
