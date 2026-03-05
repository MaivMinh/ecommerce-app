package com.minh.order_service.DTOs;

import com.minh.order_service.enums.PromotionStatus;
import com.minh.order_service.enums.PromotionType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class PromotionDTO {
    private String id;
    @NotBlank
    private String code;
    private PromotionType type;
    private Double discountValue;
    private Timestamp startDate;
    private Timestamp endDate;
    private Integer usageLimit;
    private Integer usageCount;
    private PromotionStatus status;

}
