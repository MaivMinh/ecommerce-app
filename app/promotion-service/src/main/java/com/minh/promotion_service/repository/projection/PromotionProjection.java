package com.minh.promotion_service.repository.projection;

import java.sql.Timestamp;

public interface PromotionProjection {
    String getId();
    String getCode();
    String getType();
    String getDiscountValue();
    String getMinOrderValue();
    Timestamp getStartDate();
    Timestamp getEndDate();
    Integer getUsageLimit();
    Integer getUsageCount();
    String getStatus();
}
