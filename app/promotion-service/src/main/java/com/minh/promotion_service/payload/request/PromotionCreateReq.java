package com.minh.promotion_service.payload.request;

import com.minh.promotion_service.enums.PromotionStatus;
import com.minh.promotion_service.enums.PromotionType;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class PromotionCreateReq {
    private String promotionId;
    private String code;
    private PromotionType type;
    private Double discountValue;
    private Double minOrderValue;
    private Timestamp startDate;
    private Timestamp endDate;
    private Integer usageLimit;
    private Integer usageCount;
    private PromotionStatus status;
}
