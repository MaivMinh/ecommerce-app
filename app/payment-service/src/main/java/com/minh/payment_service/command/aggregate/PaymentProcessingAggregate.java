package com.minh.payment_service.command.aggregate;

import com.minh.common.commands.ProcessPaymentCommand;
import com.minh.common.commands.RollbackProcessPaymentCommand;
import com.minh.common.events.PaymentProcessedEvent;
import com.minh.common.events.PaymentProcessedRollbackEvent;
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
public class PaymentProcessingAggregate {
    @AggregateIdentifier
    private String paymentId;
    private String orderId;
    private String orderPromotionId;
    private String reserveProductId;
    private Double total;
    private String currency;
    private String paymentMethodId;
    private String errorMsg;

    public PaymentProcessingAggregate() {
    }

    @CommandHandler
    public PaymentProcessingAggregate(ProcessPaymentCommand command) {
        PaymentProcessedEvent event = new PaymentProcessedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }
    @EventSourcingHandler
    public void on(PaymentProcessedEvent event) {
        this.paymentId = event.getPaymentId();
        this.orderId = event.getOrderId();
        this.orderPromotionId = event.getOrderPromotionId();
        this.reserveProductId = event.getReserveProductId();
        this.total = event.getTotal();
        this.currency = event.getCurrency();
        this.paymentMethodId = event.getPaymentMethodId();
        this.errorMsg = null;
    }

    @CommandHandler
    public void handle(RollbackProcessPaymentCommand command) {
        log.info("Handling RollbackProcessPaymentCommand: {}", command);
        PaymentProcessedRollbackEvent event = new PaymentProcessedRollbackEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }
    @EventSourcingHandler
    public void on(PaymentProcessedRollbackEvent event) {
        this.errorMsg = event.getErrorMsg();
    }
}
