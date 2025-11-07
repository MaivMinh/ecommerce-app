package com.minh.payment_service.command.aggregate;

import com.minh.payment_service.command.commands.CreatePaymentMethodCommand;
import com.minh.payment_service.command.commands.DeletePaymentMethodCommand;
import com.minh.payment_service.command.commands.UpdatePaymentMethodCommand;
import com.minh.payment_service.command.events.PaymentMethodCreatedEvent;
import com.minh.payment_service.command.events.PaymentMethodDeletedEvent;
import com.minh.payment_service.command.events.PaymentMethodUpdatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Aggregate
@Service
public class PaymentMethodAggregate {
    @AggregateIdentifier
    private String id;
    private String name;
    private String code;
    private String type;
    private String provider;
    private String currency;
    private String description;
    private Boolean isActive;
    private String iconUrl;
    private String errorMsg;

    public PaymentMethodAggregate() {
    }

    @CommandHandler
    public PaymentMethodAggregate(CreatePaymentMethodCommand command) {
        PaymentMethodCreatedEvent event = new PaymentMethodCreatedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }
    @EventSourcingHandler
    public void on(PaymentMethodCreatedEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.isActive = event.getIsActive();
        this.code = event.getCode();
        this.type = event.getType();
        this.provider = event.getProvider();
        this.currency = event.getCurrency();
        this.iconUrl = event.getIconUrl();
        this.errorMsg = null;
    }

    @CommandHandler
    public void handle(UpdatePaymentMethodCommand command) {
        PaymentMethodUpdatedEvent event = new PaymentMethodUpdatedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }
    @EventSourcingHandler
    public void on(PaymentMethodUpdatedEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.isActive = event.getIsActive();
        this.code = event.getCode();
        this.type = event.getType();
        this.provider = event.getProvider();
        this.currency = event.getCurrency();
        this.iconUrl = event.getIconUrl();
        this.errorMsg = null;
    }

    @CommandHandler
    public void handle(DeletePaymentMethodCommand command) {
        // You can add validation logic here if needed
        PaymentMethodDeletedEvent event = new PaymentMethodDeletedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }
    @EventSourcingHandler
    public void on(PaymentMethodDeletedEvent event) {
        this.id = null;
        this.name = null;
        this.description = null;
        this.isActive = null;
        this.code = null;
        this.type = null;
        this.provider = null;
        this.currency = null;
        this.iconUrl = null;
        this.errorMsg = null;
    }


}
