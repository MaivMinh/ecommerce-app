package com.minh.promotion_service.command.aggregate;

import com.minh.common.commands.ApplyPromotionCommand;
import com.minh.common.commands.RollbackApplyPromotionCommand;
import com.minh.common.events.PromotionAppliedEvent;
import com.minh.common.events.PromotionApplyRollbackedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Aggregate
@Service
public class OrderPromotionAggregate {
    @AggregateIdentifier
    private String orderPromotionId;
    private String reserveProductId;
    private String orderId;
    private String promotionId;
    private String paymentMethodId;
    private Double amount;
    private String currency;
    private String errorMsg;

    public OrderPromotionAggregate() {
        // Default constructor for Axon framework
    }

    @CommandHandler
    public OrderPromotionAggregate(ApplyPromotionCommand command) {
        log.info("Handling ApplyPromotionCommand: {}", command);
        PromotionAppliedEvent event = new PromotionAppliedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(PromotionAppliedEvent event) {
        this.orderPromotionId = event.getOrderPromotionId();
        this.reserveProductId = event.getReserveProductId();
        this.orderId = event.getOrderId();
        this.promotionId = event.getPromotionId();
        this.paymentMethodId = event.getPaymentMethodId();
        this.amount = event.getTotal();
        this.currency = event.getCurrency();
    }

    @CommandHandler
    public void handle(RollbackApplyPromotionCommand command) {
        log.info("Handling RollbackApplyPromotionCommand: {}", command);
        PromotionApplyRollbackedEvent event = new PromotionApplyRollbackedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }
    @EventSourcingHandler
    public void on(PromotionApplyRollbackedEvent event) {
        this.errorMsg = event.getErrorMsg();
    }

}
