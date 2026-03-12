package com.minh.order_service.controller;

import com.minh.common.constants.ResponseMessages;
import com.minh.common.response.ResponseData;
import com.minh.order_service.enums.OrderStatus;
import com.minh.order_service.payload.request.CreateOrderRequest;
import com.minh.order_service.payload.request.SearchOrdersForUserRequest;
import com.minh.order_service.payload.request.SearchOrdersRequest;
import com.minh.order_service.payload.response.OrderDetailRes;
import com.minh.order_service.payload.request.GetOrderDetailQuery;
import com.minh.order_service.payload.request.SearchOrdersForUserQuery;
import com.minh.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/orders")
@Validated
public class OrderController {
    private final OrderService service;

    @PostMapping(value = "")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        ResponseData response = service.createOrder(request);
        return ResponseEntity.status(response.getStatus())
                .body(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ResponseData> getOrderDetail(@PathVariable String id) {
        GetOrderDetailQuery query = GetOrderDetailQuery.builder().orderId(id).build();
        OrderDetailRes orderDetail = service.getOrderDetail(query);
        ResponseData response = ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(orderDetail)
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> getOrders(@RequestBody SearchOrdersRequest request) {
        ResponseData response = service.searchOrders(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/search")
    public ResponseEntity<ResponseData> getOrdersForUser(@RequestBody SearchOrdersForUserRequest request) {
        SearchOrdersForUserQuery query = SearchOrdersForUserQuery.builder()
                .keyword(request.getKeyword())
                .build();
        if (StringUtils.hasText(request.getStatus())) {
            query.setStatus(OrderStatus.valueOf(request.getStatus()));
        }
        query.setPage(request.getPage());
        query.setSize(request.getSize());
        query.setSortBy(request.getSortBy());
        query.setSortDirection(request.getSortDirection());
        ResponseData response = service.searchOrdersForUser(query);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}