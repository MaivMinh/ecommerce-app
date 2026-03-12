package com.minh.order_service.service.impl;

import com.minh.common.DTOs.OrderItemCreatedRequest;
import com.minh.common.constants.ErrorCode;
import com.minh.common.constants.ResponseMessages;
import com.minh.common.events.OrderCompletionFailedEvent;
import com.minh.common.events.OrderCreatedEvent;
import com.minh.common.events.OrderItemCreatedEvent;
import com.minh.common.functions.input.NotifyOrderCancelledEvent;
import com.minh.common.functions.input.NotifyOrderCompletedEvent;
import com.minh.common.functions.input.OrderedItem;
import com.minh.common.kafka.KafkaTopics;
import com.minh.common.message.MessageCommon;
import com.minh.common.response.ResponseData;
import com.minh.common.utils.AppUtils;
import com.minh.order_service.DTOs.OrderPromotionDto;
import com.minh.order_service.DTOs.PromotionDTO;
import com.minh.order_service.entity.Order;
import com.minh.order_service.entity.OrderItem;
import com.minh.order_service.entity.OrderPromotion;
import com.minh.order_service.entity.OrderSagaState;
import com.minh.order_service.enums.*;
import com.minh.order_service.grpc.client.PaymentGrpcClient;
import com.minh.order_service.grpc.client.ProductGrpcClient;
import com.minh.order_service.grpc.client.SupportGrpcClient;
import com.minh.order_service.payload.request.*;
import com.minh.order_service.payload.response.OrderDetailRes;
import com.minh.order_service.payload.response.OrderItemRes;
import com.minh.order_service.payload.response.ShippingAddressRes;
import com.minh.order_service.repository.OrderPromotionRepository;
import com.minh.order_service.repository.OrderRepository;
import com.minh.order_service.saga.SagaOrchestrator;
import com.minh.order_service.service.*;
import game_service.GetShippingAddressRequest;
import game_service.GetShippingAddressResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import payment_service.GetPaymentStatusRequest;
import payment_service.GetPaymentStatusResponse;
import product_service.FindProductVariantByListProductVariantIdRequest;
import product_service.FindProductVariantByListProductVariantIdResponse;
import product_service.OrderItemAndProductVariantId;
import product_service.ProductVariantRes;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final MessageCommon messageCommon;
    private final OrderItemService orderItemService;
    private final ProductGrpcClient productGrpcClient;
    private final SupportGrpcClient supportGrpcClient;
    private final PaymentGrpcClient paymentGrpcClient;
    private final PromotionService promotionService;
    private final OrderPromotionRepository orderPromotionRepository;

    private final SagaOrchestrator orchestrator;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderPromotionService orderPromotionService;
    private final OrderSagaStateService orderSagaStateService;

    private void rollbackUsedPromotion(String orderId) {
        List<OrderPromotion> orderPromotions = orderPromotionRepository.findAllByOrderId(orderId);

        OrderPromotion orderPromotion = orderPromotions.stream()
                .filter(OrderPromotion::getIsUsed)
                .findFirst()
                .orElse(null);

        if (Objects.isNull(orderPromotion)) {
            log.info("No promotion applied for this order: {}", orderId);
            return;
        }

        String promotionId = orderPromotion.getPromotionId();
        PromotionDTO promotionDTO = promotionService.findById(promotionId);
        if (Objects.isNull(promotionDTO)) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.Promotion.NOT_FOUND, promotionId));
        }
        promotionDTO.setUsageCount(promotionDTO.getUsageCount() + 1);
        promotionService.updatePromotion(promotionDTO);

        /// Update all order promotions status to false.
        orderPromotionRepository.updateIsUsedByIds(Boolean.FALSE, List.of(orderPromotion.getId()));
    }

    public ResponseData findOverallStatusOfCreatingOrder(FindOverallStatusOfCreatingOrderQuery query) {
        Order order = orderRepository.findById(query.getOrderId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + query.getOrderId()));
        return ResponseData.builder()
                .status(200)
                .message("Lấy thông tin đơn hàng thành công")
                .data(Collections.singletonMap("orderStatus", order.getStatus()))
                .build();
    }

    @Override
    public OrderDetailRes getOrderDetail(GetOrderDetailQuery query) {
        try {
            Order order = orderRepository.findById(query.getOrderId())
                    .orElseThrow(() -> new RuntimeException(messageCommon.getMessage(ErrorCode.Order.NOT_FOUND, query.getOrderId())));

            OrderDetailRes response = new OrderDetailRes();
            response.setId(order.getId());
            response.setCurrency(order.getCurrency());
            response.setDiscount(order.getDiscount());
            response.setNote(order.getNote());
            response.setStatus(order.getStatus().name());
            response.setSubTotal(order.getSubTotal());
            response.setTotal(order.getTotal());
            response.setItems(new ArrayList<>());
            response.setCreatedAt(order.getCreatedAt());
            response.setCreatedBy(order.getCreatedBy());

            //Get Shipping Address.
            GetShippingAddressRequest request = GetShippingAddressRequest.newBuilder()
                    .setShippingAddressId(order.getShippingAddressId())
                    .build();

            GetShippingAddressResponse shippingAddressGrpcRes = supportGrpcClient.getShippingAddress(request);

            if (shippingAddressGrpcRes.getStatus() == HttpStatus.NOT_FOUND.value()) {
                throw new RuntimeException(messageCommon.getMessage(ErrorCode.Address.NOT_FOUND, order.getShippingAddressId()));
            }
            if (shippingAddressGrpcRes.getStatus() != HttpStatus.OK.value()) {
                throw new RuntimeException(shippingAddressGrpcRes.getMessage());
            }

            ShippingAddressRes shippingAddressRes = new ShippingAddressRes();
            shippingAddressRes.setId(shippingAddressGrpcRes.getId());
            shippingAddressRes.setAddress(shippingAddressGrpcRes.getAddress());
            shippingAddressRes.setFullName(shippingAddressGrpcRes.getFullName());
            shippingAddressRes.setPhone(shippingAddressGrpcRes.getPhone());
            response.setShippingAddress(shippingAddressRes);

            /// Get product variant for each item.
            List<OrderItem> items = orderItemService.getAllByOrderId(order.getId());
            /// Mapping id -> OrderItem.
            Map<String, OrderItem> itemMap = items.stream()
                    .collect(java.util.stream.Collectors.toMap(OrderItem::getId, item -> item));


            List<OrderItemAndProductVariantId> ids = items.stream().map(item -> OrderItemAndProductVariantId.newBuilder()
                    .setOrderItemId(item.getId())
                    .setProductVariantId(item.getProductVariantId())
                    .build()).toList();
            FindProductVariantByListProductVariantIdRequest req = FindProductVariantByListProductVariantIdRequest.newBuilder()
                    .addAllIds(ids)
                    .build();

            FindProductVariantByListProductVariantIdResponse res = productGrpcClient.findProductVariantByListId(req);
            if (res.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                throw new RuntimeException(messageCommon.getMessage(ErrorCode.INTERNAL_SERVER_ERROR));
            }

            List<product_service.ProductVariantRes> productVariantRes = res.getProductVariantsList();
            List<OrderItemRes> orderItemResList = new ArrayList<>();
            for (ProductVariantRes productVariant : productVariantRes) {
                OrderItem orderItem = itemMap.get(productVariant.getOrderItemId());
                OrderItemRes itemRes = new OrderItemRes();
                itemRes.setId(orderItem.getId());
                itemRes.setPrice(orderItem.getPrice());
                itemRes.setQuantity(orderItem.getQuantity());
                itemRes.setTotal(orderItem.getTotal());
                itemRes.setProductVariant(com.minh.order_service.payload.response.ProductVariantRes.builder()
                        .id(productVariant.getId())
                        .colorHex(productVariant.getColorHex())
                        .colorName(productVariant.getColorName())
                        .cover(productVariant.getCover())
                        .name(productVariant.getName())
                        .originalPrice(productVariant.getOriginalPrice())
                        .price(productVariant.getPrice())
                        .size(productVariant.getSize())
                        .slug(productVariant.getSlug())
                        .build());
                orderItemResList.add(itemRes);
            }
            response.setItems(orderItemResList);

            /// Get Payment status of order.
            GetPaymentStatusRequest paymentStatusRequest = GetPaymentStatusRequest.newBuilder()
                    .setOrderId(order.getId())
                    .build();
            GetPaymentStatusResponse paymentStatusResponse = paymentGrpcClient.getPaymentStatus(paymentStatusRequest);
            if (paymentStatusResponse.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                throw new RuntimeException(messageCommon.getMessage(paymentStatusResponse.getMessage()));
            }

            if (StringUtils.hasText(paymentStatusResponse.getPaymentStatus())) {
                response.setPaymentStatus(PaymentStatus.valueOf(paymentStatusResponse.getPaymentStatus()));
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseData searchOrders(SearchOrdersRequest request) {
        Pageable pageable = AppUtils.toPageable(request);
        Page<String> orderIds = orderRepository.searchOrderIds(request, pageable);

        List<OrderDetailRes> response = new ArrayList<>();
        for (String orderId : orderIds.getContent()) {
            GetOrderDetailQuery query = GetOrderDetailQuery.builder()
                    .orderId(orderId)
                    .build();
            OrderDetailRes orderDetail = getOrderDetail(query);
            response.add(orderDetail);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("orders", response);
        data.put("totalElements", orderIds.getTotalElements());
        data.put("totalPages", orderIds.getTotalPages());
        data.put("size", orderIds.getSize());
        data.put("page", orderIds.getNumber() + 1);

        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(data)
                .build();
    }

    @Override
    public ResponseData searchOrdersForUser(SearchOrdersForUserQuery query) {
        String currentUser = AppUtils.getUsername();
        query.setCreatedBy(currentUser);
        Pageable pageable = AppUtils.toPageable(query);
        Page<String> orderIds = orderRepository.searchOrderIdsForUser(query, pageable);

        List<OrderDetailRes> response = new ArrayList<>();
        for (String orderId : orderIds.getContent()) {
            GetOrderDetailQuery param = GetOrderDetailQuery.builder()
                    .orderId(orderId)
                    .build();
            OrderDetailRes orderDetail = getOrderDetail(param);
            response.add(orderDetail);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("orders", response);
        data.put("totalElements", orderIds.getTotalElements());
        data.put("totalPages", orderIds.getTotalPages());
        data.put("size", orderIds.getSize());
        data.put("page", orderIds.getNumber() + 1);

        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(data)
                .build();
    }


    @Override
    @Transactional
    public ResponseData createOrder(CreateOrderRequest request) {
        Order order = new Order();
        String orderId = AppUtils.generateUUIDv7();
        if (!StringUtils.hasText(orderId)) {
            throw new RuntimeException("Gặp lỗi khi tạo mã đơn hàng. Vui lòng thử lại sau!");
        }

        modelMapper.map(request, order);
        order.setId(orderId);
        order.setStatus(OrderStatus.CREATED);
        orderRepository.save(order);

        /// Store order items in DB.
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemCreatedRequest dto : request.getOrderItemDtos()) {
            OrderItem item = new OrderItem();
            modelMapper.map(dto, item);
            item.setOrderId(orderId);
            items.add(item);
            item.setId(AppUtils.generateUUIDv7());
        }
        orderItemService.saveAll(items);

        /// Apply promotion when request has promotion code.
        if (StringUtils.hasText(request.getPromotionId())) {
            log.info("Applying promotion with id: {} for order id: {}", request.getPromotionId(), orderId);

            /// Find promotion.
            PromotionDTO promotion = promotionService.findById(request.getPromotionId());
            if (Objects.nonNull(promotion)) {
                log.info("Promotion with id: {} found for order id: {}", request.getPromotionId(), orderId);
                if (Objects.nonNull(promotion.getUsageCount()) && promotion.getUsageCount() > 0) {
                    OrderPromotionDto entity = new OrderPromotionDto();
                    entity.setId(AppUtils.generateUUIDv7());
                    entity.setOrderId(orderId);
                    entity.setPromotionId(promotion.getId());
                    entity.setIsUsed(Boolean.TRUE);
                    entity.setUsedAt(Instant.now());
                    orderPromotionService.applyPromotion(entity);
                    /// Decrease usage count of this promotion.
                    promotion.setUsageCount(promotion.getUsageCount() - 1);
                    promotionService.updatePromotion(promotion);
                }
            }
        }

        /// Apply voucher when request has voucher code.
        if (StringUtils.hasText(request.getVoucherId())) {
            /// Code will be implemented here.
        }

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(orderId)
                .currency(request.getCurrency())
                .orderItemDtos(request.getOrderItemDtos().stream().map(item -> OrderItemCreatedEvent.builder()
                                .id(item.getId())
                                .productVariantId(item.getProductVariantId())
                                .quantity(item.getQuantity())
                                .build())
                        .toList())
                .paymentMethod(request.getPaymentMethod())
                .productId(request.getProductId())
                .total(request.getTotal())
                .username(AppUtils.getUsername())
                .build();

        orchestrator.startSaga(event);

        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message(ResponseMessages.SUCCESS)
                .data(null)
                .build();
    }

    @Override
    @Transactional
    public void rejectOrder(OrderSagaState state) {
        try {
            if (state.getStatus().equals(SagaStatus.COMPLETED) || state.getStatus().equals(SagaStatus.FAILED)) {
                log.error("Saga {} has already been completed or failed. No need to reject order.", state.getSagaId());
                return;
            }
            if (Objects.isNull(state) || !StringUtils.hasText(state.getSagaId()) || !StringUtils.hasText(state.getOrderId())) {
                throw new RuntimeException("Saga state is null or has invalid sagaId");
            }
            orderSagaStateService.save(state);

            /// Update created order.
            int updatedRows = orderRepository.updateStatusById(OrderStatus.CANCELLED, state.getOrderId());
            if (updatedRows == 0) {
                throw new RuntimeException(messageCommon.getMessage(ErrorCode.Order.UPDATE_FAILED, state.getOrderId()));
            }

            /// Delete created order items.
            int removedItems = orderItemService.deleteAllByOrderId(state.getOrderId());
            if (removedItems == 0) {
                log.warn("No order items found to delete for order id: {}", state.getOrderId());
            }

            /// Update promotion if applied.
            this.rollbackUsedPromotion(state.getOrderId());

            /// Send notification to user about order rejection.
            List<OrderItem> orderItems = orderItemService.getAllByOrderId(state.getOrderId());
            NotifyOrderCancelledEvent event = NotifyOrderCancelledEvent.builder()
                    .orderId(state.getOrderId())
                    .items(orderItems.stream().map(
                            item -> OrderedItem.builder()
                                    .id(item.getId())
                                    .productVariantId(item.getProductVariantId())
                                    .quantity(item.getQuantity())
                                    .price(item.getPrice())
                                    .build()
                    ).toList())
                    .build();
            event.setTemplateCode(NotifyTemplateCode.ORDER_CANCELLED.name());
            event.setRecipient(Map.of("username", state.getUsername()));
            kafkaTemplate.send(KafkaTopics.NOTIFY_ORDER_CANCELLED, state.getSagaId(), event);
        } catch (RuntimeException e) {
            log.error("Error while rejecting order for saga {}: {}", state.getSagaId(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void completeOrder(OrderSagaState state) {
        if (state.getStatus().equals(SagaStatus.COMPLETED) || state.getStatus().equals(SagaStatus.FAILED)) {
            log.error("Saga {} has already been completed or failed. No need to reject order.", state.getSagaId());
            return;
        }
        if (!state.getCurrentStep().equals(SagaStep.PAYMENT_PROCESSED)) {
            log.error("Invalid saga step for saga {}. Expected: {}, actual: {}", state.getSagaId(), SagaStep.PAYMENT_PROCESSED, state.getCurrentStep());
            return;
        }
        if (!StringUtils.hasText(state.getSagaId()) || !StringUtils.hasText(state.getOrderId())) {
            throw new RuntimeException("Saga state is null or has invalid sagaId");
        }
        state.setStatus(SagaStatus.COMPLETED);
        state.setCurrentStep(SagaStep.ORDER_COMPLETED);
        orderSagaStateService.save(state);
        Order order = orderRepository.findById(state.getOrderId())
                .orElseThrow(() -> new RuntimeException(messageCommon.getMessage(ErrorCode.Order.NOT_FOUND, state.getOrderId())));
        order.setStatus(OrderStatus.SUCCESS);
        orderRepository.save(order);

        List<OrderItem> orderItems = orderItemService.getAllByOrderId(order.getId());

        /// Send notification to user about order completion.
        NotifyOrderCompletedEvent event = NotifyOrderCompletedEvent.builder()
                .orderId(state.getOrderId())
                .items(orderItems.stream().map(
                        item -> OrderedItem.builder()
                                .id(item.getId())
                                .productVariantId(item.getProductVariantId())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .build()
                ).toList())
                .build();
        event.setTemplateCode(NotifyTemplateCode.ORDER_CONFIRMATION.name());
        event.setRecipient(Map.of("username", state.getUsername()));
        kafkaTemplate.send(KafkaTopics.ORDER_COMPLETED, state.getSagaId(), event);
    }
}