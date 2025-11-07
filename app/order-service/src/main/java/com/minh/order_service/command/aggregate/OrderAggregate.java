package com.minh.order_service.command.aggregate;

import com.minh.common.commands.ConfirmCreateOrderCommand;
import com.minh.common.commands.RollbackCreateOrderCommand;
import com.minh.common.constants.AppConstants;
import com.minh.common.events.OrderCreatedEvent;
import com.minh.common.events.OrderCreatedRollbackedEvent;
import com.minh.common.events.CreatedOrderConfirmedEvent;
import com.minh.common.functions.input.NotifyOrderConfirmedEvent;
import com.minh.common.functions.input.OrderCreatedEventInput;
import com.minh.order_service.command.commands.CreateOrderCommand;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.messaging.MetaData;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Aggregate
@Service
public class OrderAggregate {
    @AggregateIdentifier
    private String orderId;
    private String username;
    private String shippingAddressId;
    private String currency;
    private Double discount;
    private Double total;
    private Double subTotal;
    private String note;
    private String paymentMethodId;
    private String promotionId;
    private String errMsg;
    private StreamBridge streamBridge;
    private String productId;

    public OrderAggregate() {
    }

    @Autowired
    public void setStreamBridge(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }


    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) {
        log.info("Handling CreateOrderCommand: {}", command);
        OrderCreatedEvent event = new OrderCreatedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.orderId = event.getOrderId();
        this.shippingAddressId = event.getShippingAddressId();
        this.currency = event.getCurrency();
        this.discount = event.getDiscount();
        this.total = event.getTotal();
        this.subTotal = event.getSubTotal();
        this.note = event.getNote();
        this.paymentMethodId = event.getPaymentMethodId();
        this.promotionId = event.getPromotionId();
        this.errMsg = null;
    }

    @CommandHandler
    public void handle(RollbackCreateOrderCommand command) {
        log.info("Handling RollbackCreateOrderCommand: {}", command);
        OrderCreatedRollbackedEvent event = new OrderCreatedRollbackedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(OrderCreatedRollbackedEvent event) {
        this.errMsg = event.getErrorMsg();
    }

    @CommandHandler
    public void handle(ConfirmCreateOrderCommand command) {
        log.info("Handling ConfirmCreateOrderCommand: {}", command);
        CreatedOrderConfirmedEvent event = new CreatedOrderConfirmedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(CreatedOrderConfirmedEvent event) {
    }
}
