package com.minh.product_service.command.aggregate;

import com.minh.common.DTOs.ReserveProductItem;
import com.minh.common.commands.ConfirmReservedProductCommand;
import com.minh.common.commands.ReserveProductCommand;
import com.minh.common.commands.ReserveProductConfirmedEvent;
import com.minh.common.commands.RollbackReserveProductCommand;
import com.minh.common.events.ProductReservedRollbackedEvent;
import com.minh.common.events.ProductReservedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Aggregate
@Service
public class ReserveProductAggregate {
    @AggregateIdentifier
    private String reserveProductId;
    private String orderId;
    private String promotionId;
    private String paymentMethodId;
    private Double total;
    private List<ReserveProductItem> reserveProductItems;
    private String currency;
    private String errorMsg;

    public ReserveProductAggregate() {
    }

    @CommandHandler
    public ReserveProductAggregate(ReserveProductCommand command) {
        log.info("Handling ReserveProductCommand: {}", command);
        ProductReservedEvent event = new ProductReservedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(ProductReservedEvent event) {
        this.reserveProductId = event.getReserveProductId();
        this.orderId = event.getOrderId();
        this.promotionId = event.getPromotionId();
        this.paymentMethodId = event.getPaymentMethodId();
        this.total = event.getTotal();
        this.currency = event.getCurrency();
        this.reserveProductItems = event.getReserveProductItems();
    }

    @CommandHandler
    public void handle(ConfirmReservedProductCommand command) {
        log.info("Handling ConfirmReservedProductCommand: {}", command);
        ReserveProductConfirmedEvent event = new ReserveProductConfirmedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(ReserveProductConfirmedEvent event) {
    }

    @CommandHandler
    public void handle(RollbackReserveProductCommand command) {
        log.info("Handling RollbackReserveProductCommand: {}", command);
        ProductReservedRollbackedEvent event = new ProductReservedRollbackedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(ProductReservedRollbackedEvent event) {
        this.errorMsg = event.getErrorMsg();
    }
}
