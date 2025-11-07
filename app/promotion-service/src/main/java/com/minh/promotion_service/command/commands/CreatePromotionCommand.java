package com.minh.promotion_service.command.commands;

import com.minh.promotion_service.enums.PromotionStatus;
import com.minh.promotion_service.enums.PromotionType;
import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePromotionCommand {
    @TargetAggregateIdentifier
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
