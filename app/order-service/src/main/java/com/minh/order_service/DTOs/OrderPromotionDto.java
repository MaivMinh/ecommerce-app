package com.minh.order_service.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class OrderPromotionDto {
    private String orderId;
    private String promotionId;
    private Boolean isUsed;
    private Date usedAt;
}
