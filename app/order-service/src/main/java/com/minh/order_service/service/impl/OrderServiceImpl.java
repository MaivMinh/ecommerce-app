package com.minh.order_service.service.impl;

import com.minh.common.DTOs.OrderItemCreateDto;
import com.minh.common.constants.AppConstants;
import com.minh.common.constants.ErrorCode;
import com.minh.common.constants.ResponseMessages;
import com.minh.common.events.CreatedOrderConfirmedEvent;
import com.minh.common.functions.input.NotifyOrderConfirmedParams;
import com.minh.common.events.OrderCreatedEvent;
import com.minh.common.events.OrderCreatedRollbackedEvent;
import com.minh.common.functions.input.NotifyOrderConfirmedEvent;
import com.minh.common.functions.input.OrderCreatedEventInput;
import com.minh.common.functions.input.OrderedItem;
import com.minh.common.message.MessageCommon;
import com.minh.common.response.ResponseData;
import com.minh.common.utils.AppUtils;
import com.minh.order_service.DTOs.OrderDto;
import com.minh.order_service.enums.NotifyTemplateCode;
import com.minh.order_service.enums.OrderStatus;
import com.minh.order_service.enums.PaymentStatus;
import com.minh.order_service.grpc.client.PaymentGrpcClient;
import com.minh.order_service.grpc.client.ProductGrpcClient;
import com.minh.order_service.grpc.client.SupportGrpcClient;
import com.minh.order_service.payload.response.OrderDetailRes;
import com.minh.order_service.payload.response.OrderItemRes;
import com.minh.order_service.payload.response.ShippingAddressRes;
import com.minh.order_service.query.controller.SearchOrdersRequest;
import com.minh.order_service.query.entity.Order;
import com.minh.order_service.query.entity.OrderItem;
import com.minh.order_service.query.queries.FindOverallOrderStatusQuery;
import com.minh.order_service.query.queries.FindOverallStatusOfCreatingOrderQuery;
import com.minh.order_service.query.queries.GetOrderDetailQuery;
import com.minh.order_service.query.queries.SearchOrdersForUserQuery;
import com.minh.order_service.query.repository.OrderRepository;
import com.minh.order_service.service.OrderItemService;
import com.minh.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import payment_service.GetPaymentStatusRequest;
import payment_service.GetPaymentStatusResponse;
import product_service.FindProductVariantByListProductVariantIdRequest;
import product_service.FindProductVariantByListProductVariantIdResponse;
import product_service.OrderItemAndProductVariantId;
import product_service.ProductVariantRes;
import support_service.GetShippingAddressRequest;
import support_service.GetShippingAddressResponse;

import java.math.BigDecimal;
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
    private final StreamBridge streamBridge;


    @Override
    @Transactional
    public void createOrder(OrderCreatedEvent event) {
        log.info("Creating order with id: {}", event.getOrderId());
        Order order = new Order();
        modelMapper.map(event, order);
        order.setId(event.getOrderId());
        order.setStatus(OrderStatus.CREATED);
        orderRepository.save(order);

        /// Store order items in DB.
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemCreateDto dto : event.getOrderItemDtos()) {
            OrderItem item = new OrderItem();
            modelMapper.map(dto, item);
            item.setOrderId(event.getOrderId());
            items.add(item);
            item.setId(AppUtils.generateUUIDv7());
        }
        orderItemService.saveAll(items);
    }

    @Override
    @Transactional
    public void rollbackOrderCreated(OrderCreatedRollbackedEvent event) {
        Order order = orderRepository.findById(event.getOrderId()).orElseThrow(
                () -> new RuntimeException(messageCommon.getMessage(ErrorCode.Order.NOT_FOUND, event.getOrderId()))
        );
        order.setStatus(OrderStatus.CANCELLED);

        orderItemService.removeAllByOrderId(order.getId());
    }

    @Override
    public void confirmCreatedOrder(CreatedOrderConfirmedEvent event) {
        Order saved = orderRepository.findById(event.getOrderId()).orElseThrow(
                () -> new RuntimeException(messageCommon.getMessage(ErrorCode.Order.NOT_FOUND, event.getOrderId()))
        );

        saved.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(saved);

        /// Gửi 1 event sang cho support service để service này cho phép review đơn hàng này.
        var result = streamBridge.send("publishOrderCreated-out-0", OrderCreatedEventInput.builder()
                .productId(event.getProductId())
                .username(event.getUsername())
                .build());
        if (result) {
            log.info("Gửi sự kiện tới Kafka server thành công cho đơn hàng: {}", event.getOrderId());
        } else log.warn("Có lỗi xảy ra khi gửi sự kiện tới Kafka server");

        /// Tính tổng đơn hàng.
        List<OrderItem> orderItems = orderItemService.getAllByOrderId(event.getOrderId());

        List<OrderedItem> items = new ArrayList<>();
        for (OrderItem item : orderItems) {
            items.add(OrderedItem.builder()
                    .id(item.getId())
                    .productVariantId(item.getProductVariantId())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .build());
        }


        /// Gửi event thông báo xác nhận đơn hàng.
        result = streamBridge.send("publishNotifyOrderConfirmed-out-0", NotifyOrderConfirmedEvent.builder()
                .templateCode(NotifyTemplateCode.ORDER_CONFIRMATION.name())
                .recipient(Map.of("username", event.getUsername()))
                .params(NotifyOrderConfirmedParams.builder()
                        .orderId(event.getOrderId())
                        .items(items)
                        .build()
                )
                .metaData(Map.of(
                        "createdAt", System.currentTimeMillis(),
                        "redirectUrl", AppConstants.FRONTEND_URL + "/orders/"
                ))
                .build());

        if (result) {
            log.info("Gửi thông báo xác nhận đơn hàng thành công tới Email cho username: {}", event.getUsername());
        } else {
            log.warn("Có lỗi xảy ra khi gửi thông báo xác nhận đơn hàng tới Email cho username: {}", event.getUsername());
        }

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
        String createdUser = AppUtils.getUsername();
        query.setCreatedUser(createdUser);
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
    public ResponseData findOverallOrderStatusQuery(FindOverallOrderStatusQuery query) {
        if (!StringUtils.hasText(query.getOrderId())) {
            return ResponseData.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(messageCommon.getMessage(ErrorCode.INVALID_PARAMS))
                    .data(null)
                    .build();
        }

        Order order = orderRepository.findById(query.getOrderId()).orElse(null);
        if (order == null) {
            return ResponseData.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message(messageCommon.getMessage(ErrorCode.Order.NOT_FOUND, query.getOrderId()))
                    .data(null)
                    .build();
        }
        OrderDto dto = modelMapper.map(order, OrderDto.class);
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message(ResponseMessages.SUCCESS)
                .data(dto)
                .build();
    }
}