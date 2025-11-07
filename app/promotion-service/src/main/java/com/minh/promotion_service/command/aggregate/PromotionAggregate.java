package com.minh.promotion_service.command.aggregate;

import com.minh.promotion_service.command.commands.CreatePromotionCommand;
import com.minh.promotion_service.command.events.PromotionCreatedEvent;
import com.minh.promotion_service.enums.PromotionStatus;
import com.minh.promotion_service.enums.PromotionType;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.messaging.annotation.MetaDataValue;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Aggregate
@Service
public class PromotionAggregate {
    @AggregateIdentifier
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
    private String errorMsg;

    public PromotionAggregate() {

    }

    @CommandHandler
    public PromotionAggregate(CreatePromotionCommand command) {
        PromotionCreatedEvent event = new PromotionCreatedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }
    @EventSourcingHandler
    public void on(PromotionCreatedEvent event) {
        this.promotionId = event.getPromotionId();
        this.code = event.getCode();
        this.type = event.getType();
        this.discountValue = event.getDiscountValue();
        this.minOrderValue = event.getMinOrderValue();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.usageLimit = event.getUsageLimit();
        this.usageCount = event.getUsageCount();
        this.status = event.getStatus();
    }

}
