package com.minh.order_service.query.handler;

import com.minh.common.response.ResponseData;
import com.minh.order_service.payload.response.OrderDetailRes;
import com.minh.order_service.query.controller.SearchOrdersRequest;
import com.minh.order_service.query.queries.FindOverallOrderStatusQuery;
import com.minh.order_service.query.queries.FindOverallStatusOfCreatingOrderQuery;
import com.minh.order_service.query.queries.GetOrderDetailQuery;
import com.minh.order_service.query.queries.SearchOrdersForUserQuery;
import com.minh.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderQueryHandler {
    private final OrderService orderService;

    @QueryHandler
    public ResponseData handle(FindOverallStatusOfCreatingOrderQuery query) {
        return orderService.findOverallStatusOfCreatingOrder(query);
    }

    @QueryHandler
    public OrderDetailRes handle(GetOrderDetailQuery query) {
        return orderService.getOrderDetail(query);
    }

    @QueryHandler
    public ResponseData handle(SearchOrdersRequest request) {
        return orderService.searchOrders(request);
    }

    @QueryHandler
    public ResponseData handle(SearchOrdersForUserQuery query) {
        return orderService.searchOrdersForUser(query);
    }
    @QueryHandler
    public ResponseData handle(FindOverallOrderStatusQuery query) {
        return orderService.findOverallOrderStatusQuery(query);
    }
}