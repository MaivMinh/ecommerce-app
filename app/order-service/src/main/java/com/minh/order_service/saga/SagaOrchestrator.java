package com.minh.order_service.saga;

import com.minh.common.DTOs.ReservedProductItem;
import com.minh.common.commands.ProcessPaymentCommand;
import com.minh.common.commands.RefundProcessedPaymentCommand;
import com.minh.common.commands.ReleaseProductCommand;
import com.minh.common.commands.ReserveProductCommand;
import com.minh.common.events.*;
import com.minh.common.kafka.KafkaTopics;
import com.minh.common.utils.AppUtils;
import com.minh.order_service.entity.OrderSagaState;
import com.minh.order_service.enums.SagaStatus;
import com.minh.order_service.enums.SagaStep;
import com.minh.order_service.outbox.OutboxMessageService;
import com.minh.order_service.service.OrderSagaStateService;
import com.minh.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SagaOrchestrator {

    private final com.minh.order_service.repository.OrderSagaStateRepository orderSagaStateRepository;
    private final OrderService orderService;
    private final OrderSagaStateService orderSagaStateService;
    private final OutboxMessageService outboxMessageService;

    /**
     * Bước 1: Bắt đầu Saga flow khi Order vừa được tạo.
     *
     * @param event: Thông tin event về đơn hàng vừa được tạo.
     */
    @Transactional
    public void startSaga(OrderCreatedEvent event) {
        log.info("Saga Event [1]: Received OrderCreatedEvent for order [{}]. Starting saga flow.", event.getOrderId());

        String sagaId = AppUtils.generateUUIDv7();
        event.setSagaId(sagaId);

        /// Lưu trạng thái ban đầu của Saga này vào trong DB. Trạng thái này sẽ được cập nhật dần dần khi Saga flow diễn ra.
        OrderSagaState state = OrderSagaState.builder()
                .sagaId(sagaId)
                .orderId(event.getOrderId())
                .status(SagaStatus.PROCESSING)
                .currentStep(SagaStep.ORDER_CREATED)
                .failureReason(null)
                .username(event.getUsername())
                .build();
        orderSagaStateRepository.save(state);

        /// Build event với thông tin đầy đủ để gửi sang product-service.
        ReserveProductCommand command = ReserveProductCommand.builder()
                .orderId(event.getOrderId())
                .paymentMethod(event.getPaymentMethod())
                .total(event.getTotal())
                .currency(event.getCurrency())
                .username(event.getUsername())
                .productId(event.getProductId())
                .reserveProductItems(event.getOrderItemDtos().stream().map(
                        item -> ReservedProductItem.builder()
                                .productVariantId(item.getProductVariantId())
                                .quantity(item.getQuantity())
                                .build()
                ).toList())
                .build();

        command.setSagaId(event.getSagaId());
        command.setTimestamp(event.getTimestamp());
        outboxMessageService.store(KafkaTopics.PRODUCT_RESERVE, command, ReserveProductCommand.class.getName());
        log.info("Saga [{}] started for order [{}]. Sent ReserveProductCommand to Kafka.", sagaId, event.getOrderId());
    }

    @KafkaListener(
            topics = KafkaTopics.PRODUCT_RESERVED,
            groupId = "order-service"
    )
    @Transactional
    public void handleProductReservedEvent(ProductReservedEvent event) {
        log.info("Saga Event [2]: Received ProductReservedEvent for order [{}].", event.getOrderId());

        OrderSagaState state = findSaga(event.getSagaId());
        if (!state.getCurrentStep().equals(SagaStep.ORDER_CREATED)) {
            log.error("Invalid ProductReservedEvent event for saga {}: current step is {}, expected ORDER_CREATED", event.getSagaId(), state.getCurrentStep());
            return;
        }
        /// Reserve thành công, tiếp tục thanh toán.
        state.setCurrentStep(SagaStep.PRODUCT_RESERVED);
        orderSagaStateRepository.save(state);

        ProcessPaymentCommand command = ProcessPaymentCommand.builder()
                .orderId(state.getOrderId())
                .paymentMethod(event.getPaymentMethod())
                .total(event.getTotal())
                .currency(event.getCurrency())
                .username(state.getUsername())
                .build();
        command.setSagaId(event.getSagaId());
        command.setTimestamp(event.getTimestamp());
        outboxMessageService.store(KafkaTopics.PAYMENT_PROCESS, command, ProcessPaymentCommand.class.getName());
        log.info("Saga [{}] product reserved for order [{}]. Sent ProcessPaymentCommand to Kafka.", state.getSagaId(), state.getOrderId());
    }

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_PROCESSED,
            groupId = "order-service"
    )
    @Transactional
    public void handlePaymentProcessedEvent(PaymentProcessedEvent event) {
        log.info("Saga Event [3]: Received PaymentProcessedEvent for order [{}].", event.getOrderId());

        OrderSagaState state = findSaga(event.getSagaId());
        if (!state.getCurrentStep().equals(SagaStep.PRODUCT_RESERVED)) {
            log.error("Invalid PaymentProcessedEvent event for saga {}: current step is {}, expected PRODUCT_RESERVED", event.getSagaId(), state.getCurrentStep());
            return;
        }
        state.setCurrentStep(SagaStep.PAYMENT_PROCESSED);
        OrderSagaState saved = orderSagaStateRepository.save(state);

        try {
            orderService.completeOrder(saved);
            log.info("Saga [{}] completed successfully for order [{}].", state.getSagaId(), state.getOrderId());
        } catch (RuntimeException e) {
            log.error("Error while completing order for saga {}: {}", state.getSagaId(), e.getMessage());
            OrderCompletionFailedEvent failedEvent = OrderCompletionFailedEvent.builder()
                    .orderId(state.getOrderId())
                    .paymentId(event.getPaymentId())
                    .paymentMethod(event.getPaymentMethod())
                    .errorMsg(e.getMessage())
                    .build();
            failedEvent.setSagaId(event.getSagaId());
            failedEvent.setTimestamp(event.getTimestamp());
            saved.setCurrentStep(SagaStep.ORDER_COMPLETION_FAILED);
            orderSagaStateRepository.save(saved);
            outboxMessageService.store(KafkaTopics.ORDER_COMPLETION_FAILED, failedEvent, OrderCompletionFailedEvent.class.getName());
        }
    }


    /// =========================== COMPENSATING ACTIONS =========================== ///

    @KafkaListener(
            topics = KafkaTopics.ORDER_COMPLETION_FAILED,
            groupId = "order-service"
    )
    @Transactional
    public void handleOrderCancelledEvent(OrderCompletionFailedEvent event) {
        log.info("Saga Event Rollback: Received OrderCompletionFailedEvent for order [{}]. Starting compensation flow.", event.getOrderId());

        OrderSagaState state = findSaga(event.getSagaId());
        if (!state.getCurrentStep().equals(SagaStep.ORDER_COMPLETION_FAILED)) {
            log.error("Invalid OrderCompletionFailedEvent event for saga {}: current step is {}, expected ORDER_COMPLETION_FAILED", event.getSagaId(), state.getCurrentStep());
            return;
        }

        RefundProcessedPaymentCommand command = RefundProcessedPaymentCommand.builder()
                .orderId(state.getOrderId())
                .paymentId(event.getPaymentId())
                .paymentMethod(event.getPaymentMethod())
                .username(event.getUsername())
                .build();
        command.setSagaId(event.getSagaId());
        command.setTimestamp(event.getTimestamp());
        outboxMessageService.store(KafkaTopics.PAYMENT_REFUND, command, RefundProcessedPaymentCommand.class.getName());
        log.info("Saga [{}] processing compensation for order [{}]. Sent RefundProcessedPaymentCommand to Kafka.", state.getSagaId(), state.getOrderId());
    }

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_FAILED,
            groupId = "order-service"
    )
    @Transactional
    public void handlePaymentFailedEvent(PaymentFailedEvent event) {
        log.info("Saga Event Rollback: Received PaymentFailedEvent for order [{}]. ", event.getOrderId());
        OrderSagaState state = findSaga(event.getSagaId());
        if (!state.getCurrentStep().equals(SagaStep.PRODUCT_RESERVED)) {
            log.error("Invalid PaymentFailedEvent event for saga {}: current step is {}, expected PRODUCT_RESERVED", event.getSagaId(), state.getCurrentStep());
            return;
        }
        state.setCurrentStep(SagaStep.PAYMENT_FAILED);
        orderSagaStateRepository.save(state);

        ReleaseProductCommand command = ReleaseProductCommand.builder()
                .orderId(state.getOrderId())
                .username(event.getUsername())
                .errorMsg(event.getErrorMsg())
                .build();
        command.setSagaId(event.getSagaId());
        command.setTimestamp(event.getTimestamp());
        outboxMessageService.store(KafkaTopics.PRODUCT_RELEASE, command, ReleaseProductCommand.class.getName());
        log.info("Saga [{}] compensation completed for order [{}].", state.getSagaId(), state.getOrderId());
    }

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_REFUNDED,
            groupId = "order-service"
    )
    @Transactional
    public void handlePaymentRefundedEvent(PaymentRefundedEvent event) {
        log.info("Saga Event Rollback: Received PaymentRefundedEvent for order [{}]. Continuing compensation flow.", event.getOrderId());

        OrderSagaState state = findSaga(event.getSagaId());
        if (!state.getCurrentStep().equals(SagaStep.ORDER_COMPLETION_FAILED)) {
            log.error("Invalid PaymentRefundedEvent event for saga {}: current step is {}, expected ORDER_COMPLETION_FAILED", event.getSagaId(), state.getCurrentStep());
            return;
        }
        state.setCurrentStep(SagaStep.PAYMENT_REFUNDED);
        orderSagaStateRepository.save(state);

        /// Create product release event để gửi sang product-service, yêu cầu release lại sản phẩm đã reserve.
        ReleaseProductCommand command = ReleaseProductCommand.builder()
                .orderId(state.getOrderId())
                .username(event.getUsername())
                .errorMsg(event.getErrorMsg())
                .build();
        command.setSagaId(event.getSagaId());
        command.setTimestamp(event.getTimestamp());

        /// Thanh toán bị refund, cần release lại sản phẩm đã reserve.
        outboxMessageService.store(KafkaTopics.PRODUCT_RELEASE, command, ReleaseProductCommand.class.getName());
        log.info("Saga [{}] processing compensation for order [{}]. Sent ProductReleaseCommand to Kafka.", state.getSagaId(), state.getOrderId());
    }


    @KafkaListener(
            topics = KafkaTopics.PRODUCT_RELEASED,
            groupId = "order-service"
    )
    @Transactional
    public void handleProductReleasedEvent(ProductReleasedEvent event) {
        log.info("Saga Event Rollback: Received ProductReleasedEvent for order [{}]. Finalizing compensation flow.", event.getOrderId());

        OrderSagaState state = findSaga(event.getSagaId());
        if (!state.getCurrentStep().equals(SagaStep.PAYMENT_REFUNDED) && !state.getCurrentStep().equals(SagaStep.PAYMENT_FAILED)) {
            log.error("Invalid ProductReleasedEvent event for saga {}: current step is {}, expected PAYMENT_REFUNDED", event.getSagaId(), state.getCurrentStep());
            return;
        }
        state.setCurrentStep(SagaStep.PRODUCT_RELEASED);
        orderService.rejectOrder(state);
        log.info("Saga [{}] compensation completed for order [{}].", state.getSagaId(), state.getOrderId());
    }

    @KafkaListener(
            topics = KafkaTopics.PRODUCT_RESERVATION_FAILED,
            groupId = "order-service"
    )
    @Transactional
    public void handleProductInsufficientEvent(ProductReservationFailedEvent event) {
        log.info("Saga Event Rollback: Received ProductReservationFailedEvent for order [{}]. Starting compensation flow.", event.getOrderId());

        OrderSagaState state = findSaga(event.getSagaId());
        if (!state.getCurrentStep().equals(SagaStep.ORDER_CREATED)) {
            log.error("Invalid ProductReservationFailedEvent event for saga {}: current step is {}, expected ORDER_CREATED", event.getSagaId(), state.getCurrentStep());
            return;
        }
        state.setCurrentStep(SagaStep.PRODUCT_RESERVATION_FAILED);
        orderService.rejectOrder(state);
        log.info("Saga [{}] compensation completed for order [{}].", state.getSagaId(), state.getOrderId());
    }

    private OrderSagaState findSaga(String sagaId) {
        return orderSagaStateService.findBySagaId(sagaId);
    }
}