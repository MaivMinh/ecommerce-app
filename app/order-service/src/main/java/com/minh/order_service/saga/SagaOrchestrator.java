package com.minh.order_service.saga;

import com.minh.common.DTOs.ReservedProductItem;
import com.minh.common.commands.ProcessPaymentCommand;
import com.minh.common.commands.RefundPaymentCommand;
import com.minh.common.commands.ReleaseProductCommand;
import com.minh.common.commands.ReserveProductCommand;
import com.minh.common.events.*;
import com.minh.common.kafka.KafkaTopics;
import com.minh.common.utils.AppUtils;
import com.minh.order_service.entity.OrderSagaState;
import com.minh.order_service.enums.SagaStatus;
import com.minh.order_service.enums.SagaStep;
import com.minh.order_service.service.OrderSagaStateService;
import com.minh.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SagaOrchestrator {

    private final com.minh.order_service.repository.OrderSagaStateRepository orderSagaStateRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderService orderService;
    private final OrderSagaStateService orderSagaStateService;

    /**
     * Bước 1: Bắt đầu Saga flow khi Order vừa được tạo.
     *
     * @param event: Thông tin event về đơn hàng vừa được tạo.
     */
    public void startSaga(OrderCreatedEvent event) {
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

        kafkaTemplate.send(KafkaTopics.PRODUCT_RESERVE, sagaId, command);
        log.info("Saga [{}] started for order [{}]. Sent ReserveProductCommand to Kafka.", sagaId, event.getOrderId());
    }

    @KafkaListener(
            topics = KafkaTopics.PRODUCT_RESERVED,
            groupId = "order-service"
    )
    @Transactional
    public void handleProductReservedEvent(ProductReservedEvent event) {
        OrderSagaState state = findSaga(event.getSagaId());
        if (!state.getCurrentStep().equals(SagaStep.ORDER_CREATED))  {
            log.error("Invalid saga step for saga {}", event.getSagaId());
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

        kafkaTemplate.send(KafkaTopics.PAYMENT_PROCESS, state.getSagaId(), command);
        log.info("Saga [{}] product reserved for order [{}]. Sent ProcessPaymentCommand to Kafka.", state.getSagaId(), state.getOrderId());
    }

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_PROCESSED,
            groupId = "order-service"
    )
    @Transactional
    public void handlePaymentProcessedEvent(PaymentProcessedEvent event) {
        OrderSagaState state = findSaga(event.getSagaId());
        if (!state.getCurrentStep().equals(SagaStep.PRODUCT_RESERVED))  {
            log.error("Invalid saga step for saga {}", event.getSagaId());
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
                    .errorMsg(e.getMessage())
                    .build();
            failedEvent.setSagaId(event.getSagaId());
            failedEvent.setTimestamp(event.getTimestamp());

            saved.setCurrentStep(SagaStep.ORDER_COMPLETION_FAILED);
            orderSagaStateRepository.save(saved);
            kafkaTemplate.send(KafkaTopics.ORDER_COMPLETION_FAILED, state.getSagaId(), failedEvent);
        }

        log.info("Saga [{}] completed successfully for order [{}].", state.getSagaId(), state.getOrderId());
    }


    /// =========================== COMPENSATING ACTIONS =========================== ///

    @KafkaListener(
            topics = KafkaTopics.ORDER_COMPLETION_FAILED,
            groupId = "order-service"
    )
    @Transactional
    public void handleOrderCancelledEvent(OrderCompletionFailedEvent event) {
        OrderSagaState state = findSaga(event.getSagaId());
        if (!state.getCurrentStep().equals(SagaStep.ORDER_COMPLETION_FAILED)) {
            log.error("Invalid saga step for saga {}", event.getSagaId());
            return;
        }

        RefundPaymentCommand command = RefundPaymentCommand.builder()
                .orderId(state.getOrderId())
                .username(event.getUsername())
                .errorMsg(event.getErrorMsg())
                .build();
        command.setSagaId(event.getSagaId());
        command.setTimestamp(event.getTimestamp());

        kafkaTemplate.send(KafkaTopics.PAYMENT_REFUND, state.getSagaId(), command);
        log.info("Saga [{}] processing compensation for order [{}]. Sent RefundPaymentCommand to Kafka.", state.getSagaId(), state.getOrderId());
    }

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_REFUNDED,
            groupId = "order-service"
    )
    @Transactional
    public void handlePaymentRefundedEvent(PaymentRefundedEvent event) {
        OrderSagaState state = findSaga(event.getSagaId());
        if (!state.getCurrentStep().equals(SagaStep.ORDER_COMPLETION_FAILED))  {
            log.error("Invalid saga step for saga {}", event.getSagaId());
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
        kafkaTemplate.send(KafkaTopics.PRODUCT_RELEASE, state.getSagaId(), command);
        log.info("Saga [{}] processing compensation for order [{}]. Sent ProductReleaseCommand to Kafka.", state.getSagaId(), state.getOrderId());
    }


    @KafkaListener(
            topics = KafkaTopics.PRODUCT_RELEASED,
            groupId = "order-service"
    )
    @Transactional
    public void handleProductReleasedEvent(ProductReleasedEvent event) {
        OrderSagaState state = findSaga(event.getSagaId());
        if (!state.getCurrentStep().equals(SagaStep.PAYMENT_REFUNDED))  {
            log.error("Invalid saga step for saga {}", event.getSagaId());
            return;
        }
        state.setCurrentStep(SagaStep.PRODUCT_RELEASED);
        state.setStatus(SagaStatus.FAILED);
        orderService.rejectOrder(state);
        log.info("Saga [{}] compensation completed for order [{}].", state.getSagaId(), state.getOrderId());
    }

    @KafkaListener(
            topics = KafkaTopics.PRODUCT_RESERVATION_FAILED,
            groupId = "order-service"
    )
    @Transactional
    public void handleProductInsufficientEvent(ProductReservationFailedEvent event) {
        OrderSagaState state = findSaga(event.getSagaId());
        if (!state.getCurrentStep().equals(SagaStep.ORDER_CREATED))  {
            log.error("Invalid saga step for saga {}", event.getSagaId());
            return;
        }
        state.setCurrentStep(SagaStep.PRODUCT_RESERVATION_FAILED);
        state.setStatus(SagaStatus.FAILED);
        orderService.rejectOrder(state);
        log.info("Saga [{}] compensation completed for order [{}].", state.getSagaId(), state.getOrderId());
    }

    private OrderSagaState findSaga(String sagaId) {
        return orderSagaStateService.findBySagaId(sagaId);
    }
}