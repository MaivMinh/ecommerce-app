package com.minh.order_service.saga;

import com.minh.common.DTOs.ReserveProductItem;
import com.minh.common.commands.*;
import com.minh.common.constants.ResponseMessages;
import com.minh.common.events.*;
import com.minh.common.response.ResponseData;
import com.minh.common.utils.AppUtils;
import com.minh.order_service.query.queries.FindOverallOrderStatusQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Saga
@Slf4j
public class OrderProcessManager {
    @Autowired
    private transient CommandGateway commandGateway;
    @Autowired
    private transient QueryUpdateEmitter queryUpdateEmitter;

    /// ====================================== SAGA HAPPY FLOW ====================================== ///

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderCreatedEvent event) {
        log.info("Saga Event 1 [Start] : Received OrderCreatedEventInput for order id: {}", event.getOrderId());
        List<ReserveProductItem> reserveProductItems = new ArrayList<>();
        event.getOrderItemDtos().forEach(item -> {
            reserveProductItems.add(ReserveProductItem.builder()
                    .productVariantId(item.getProductVariantId())
                    .quantity(item.getQuantity())
                    .build());
        });

        ReserveProductCommand command = ReserveProductCommand.builder()
                .reserveProductId(AppUtils.generateUUIDv7())
                .orderId(event.getOrderId())
                .currency(event.getCurrency())
                .paymentMethodId(event.getPaymentMethodId())
                .promotionId(event.getPromotionId())
                .reserveProductItems(reserveProductItems)
                .total(event.getTotal())
                .username(event.getUsername())
                .productId(event.getProductId())
                .build();

        commandGateway.send(command, (commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                log.error("Saga Event 1 [Error] : Failed to reserve product for order id: {}, rolling back order creation.", event.getOrderId());
                RollbackCreateOrderCommand rollbackCommand = new RollbackCreateOrderCommand();
                rollbackCommand.setOrderId(event.getOrderId());
                rollbackCommand.setErrorMsg(commandResultMessage.exceptionResult().getMessage());
                commandGateway.sendAndWait(rollbackCommand);
            }
        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(ProductReservedEvent event) {
        log.info("Saga Event 2 : Received ProductReservedEvent for order id: {}", event.getOrderId());

        /// Apply promotion for the order if needed.
        ApplyPromotionCommand command = ApplyPromotionCommand.builder()
                .orderPromotionId(AppUtils.generateUUIDv7())
                .reserveProductId(event.getReserveProductId())
                .orderId(event.getOrderId())
                .promotionId(event.getPromotionId())
                .paymentMethodId(event.getPaymentMethodId())
                .total(event.getTotal())
                .currency(event.getCurrency())
                .username(event.getUsername())
                .productId(event.getProductId())
                .build();

        commandGateway.send(command, (commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                log.error("Saga Event 2 [Error] : Failed to apply promotion for order id: {}, rolling back order creation.", event.getOrderId());
                RollbackReserveProductCommand rollbackCommand = RollbackReserveProductCommand.builder()
                        .reserveProductId(event.getReserveProductId())
                        .orderId(event.getOrderId())
                        .errorMsg(commandResultMessage.exceptionResult().getMessage())
                        .build();
                commandGateway.sendAndWait(rollbackCommand);
            }
        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(PromotionAppliedEvent event) {
        log.info("Saga Event 3 : Received PromotionAppliedEvent for order id: {}", event.getOrderId());
        /// Process payment.
        ProcessPaymentCommand command = ProcessPaymentCommand.builder()
                .paymentId(AppUtils.generateUUIDv7())
                .orderPromotionId(event.getOrderPromotionId())
                .reserveProductId(event.getReserveProductId())
                .orderId(event.getOrderId())
                .paymentMethodId(event.getPaymentMethodId())
                .total(event.getTotal())
                .currency(event.getCurrency())
                .username(event.getUsername())
                .productId(event.getProductId())
                .build();

        commandGateway.send(command, (commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                log.error("Saga Event 3 [Error] : Failed to process payment for order id: {}, rolling back order creation.", event.getOrderId());
                RollbackApplyPromotionCommand rollbackCommand = RollbackApplyPromotionCommand.builder()
                        .orderPromotionId(event.getOrderPromotionId())
                        .reserveProductId(event.getReserveProductId())
                        .orderId(event.getOrderId())
                        .errorMsg(commandResultMessage.exceptionResult().getMessage())
                        .build();
                commandGateway.sendAndWait(rollbackCommand);
            }
        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(PaymentProcessedEvent event) {
        log.info("Saga Event 4 : Payment processed successfully for order id: {}", event.getOrderId());
        ConfirmReservedProductCommand command = ConfirmReservedProductCommand.builder()
                .reserveProductId(event.getReserveProductId())
                .paymentId(event.getPaymentId())
                .orderPromotionId(event.getOrderPromotionId())
                .orderId(event.getOrderId())
                .username(event.getUsername())
                .productId(event.getProductId())
                .build();

        commandGateway.send(command, (commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                log.error("Saga Event 4 [Error] : Failed to confirm reserved product for payment id: {}, rolling back order creation.", event.getPaymentId());
                RollbackProcessPaymentCommand rollbackCommand = RollbackProcessPaymentCommand.builder()
                        .paymentId(event.getPaymentId())
                        .orderPromotionId(event.getOrderPromotionId())
                        .reserveProductId(event.getReserveProductId())
                        .orderId(event.getOrderId())
                        .errorMsg(commandResultMessage.exceptionResult().getMessage())
                        .build();
                commandGateway.sendAndWait(rollbackCommand);
            }
        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(ReserveProductConfirmedEvent event) {
        log.info("Saga Event 5 : Reserve product confirmed for order id: {}", event.getOrderId());
        /// Xác nhận đặt chỗ sản phẩm thành công, cập nhật trạng thái đơn hàng trong order-service.
        log.info("Current username: {}", AppUtils.getUsername());

        ConfirmCreateOrderCommand command = ConfirmCreateOrderCommand.builder()
                .orderId(event.getOrderId())
                .paymentId(event.getPaymentId())
                .orderPromotionId(event.getOrderPromotionId())
                .reserveProductId(event.getReserveProductId())
                .username(event.getUsername())
                .productId(event.getProductId())
                .build();

        commandGateway.send(command, (commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                log.error("Saga Event 5 [Error] : Failed to confirm created order for order id: {}, rolling back payment process.", event.getOrderId());
                RollbackProcessPaymentCommand rollbackCommand = RollbackProcessPaymentCommand.builder()
                        .paymentId(event.getPaymentId())
                        .orderPromotionId(event.getOrderPromotionId())
                        .reserveProductId(event.getReserveProductId())
                        .orderId(event.getOrderId())
                        .errorMsg(commandResultMessage.exceptionResult().getMessage())
                        .build();
                commandGateway.sendAndWait(rollbackCommand);
            }
        });
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(CreatedOrderConfirmedEvent event) {
        log.info("Saga Event 6 [End] : Create order confirmed for order id: {}", event.getOrderId());

        Map<String, Object> data = new HashMap<>();
        data.put("orderId", event.getOrderId());
        data.put("paymentId", event.getPaymentId());

        queryUpdateEmitter.emit(FindOverallOrderStatusQuery.class,
                query -> true,
                ResponseData.builder()
                        .status(200)
                        .message(ResponseMessages.SUCCESS)
                        .data(data)
                        .build());
    }

    /// ====================================== ROLLBACK SAGA HANDLERS ====================================== ///

    @SagaEventHandler(associationProperty = "orderId")
    public void on(PaymentProcessedRollbackEvent event) {
        log.info("Saga Event Rollback [1] : Received PaymentProcessedRollbackEvent for order id: {}", event.getOrderId());

        /// Rollback the applied promotion command.
        RollbackApplyPromotionCommand rollbackCommand = RollbackApplyPromotionCommand.builder()
                .orderPromotionId(event.getOrderPromotionId())
                .reserveProductId(event.getReserveProductId())
                .orderId(event.getOrderId())
                .errorMsg(event.getErrorMsg())
                .build();
        commandGateway.sendAndWait(rollbackCommand);
    }


    @SagaEventHandler(associationProperty = "orderId")
    public void on(PromotionApplyRollbackedEvent event) {
        log.info("Saga Event Rollback [2] : Received PromotionApplyRollbackedEvent for order id: {}", event.getOrderId());
        /// Rollback the reserve product command.
        RollbackReserveProductCommand rollbackCommand = RollbackReserveProductCommand.builder()
                .reserveProductId(event.getReserveProductId())
                .orderId(event.getOrderId())
                .errorMsg(event.getErrorMsg())
                .build();
        commandGateway.sendAndWait(rollbackCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(ProductReservedRollbackedEvent event) {
        log.info("Saga Event Rollback [3]: Received ProductReservedRollbackedEvent for order id: {}", event.getOrderId());
        /// Rollback the order creation.
        RollbackCreateOrderCommand rollbackCommand = RollbackCreateOrderCommand.builder()
                .orderId(event.getOrderId())
                .errorMsg(event.getErrorMsg())
                .build();
        commandGateway.sendAndWait(rollbackCommand);
    }


    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderCreatedRollbackedEvent event) {
        log.info("Saga Event Rollback [END]: Received OrderCreatedRollbackedEvent for order id: {}", event.getOrderId());
        queryUpdateEmitter.emit(FindOverallOrderStatusQuery.class,
                query -> true,
                ResponseData.builder()
                        .status(500)
                        .message(ResponseMessages.INTERNAL_SERVER_ERROR)
                        .data(null)
                        .build());
    }
}