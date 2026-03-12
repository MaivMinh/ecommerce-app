package com.minh.order_service.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

@Getter
@Setter
public class OrderPromotionDto {
    private String id;
    private String orderId;
    private String promotionId;
    private Boolean isUsed;
    private Instant usedAt;
}
