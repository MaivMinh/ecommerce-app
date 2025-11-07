package com.minh.promotion_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class OrderPromotionDto {
    private String orderId;
    private String promotionId;
    private Boolean isApplied;
    private Date appliedAt;
}
