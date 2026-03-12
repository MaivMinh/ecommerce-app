package com.minh.order_service.service;

import com.minh.common.response.ResponseData;
import com.minh.order_service.entity.OrderSagaState;
import com.minh.order_service.payload.request.CreateOrderRequest;
import com.minh.order_service.payload.response.OrderDetailRes;
import com.minh.order_service.payload.request.SearchOrdersRequest;
import com.minh.order_service.payload.request.FindOverallOrderStatusQuery;
import com.minh.order_service.payload.request.FindOverallStatusOfCreatingOrderQuery;
import com.minh.order_service.payload.request.GetOrderDetailQuery;
import com.minh.order_service.payload.request.SearchOrdersForUserQuery;

public interface OrderService {
    OrderDetailRes getOrderDetail(GetOrderDetailQuery query);

    ResponseData searchOrders(SearchOrdersRequest request);

    ResponseData searchOrdersForUser(SearchOrdersForUserQuery query);

    ResponseData createOrder(CreateOrderRequest request);

    void rejectOrder(OrderSagaState state);

    void completeOrder(OrderSagaState state);
}
