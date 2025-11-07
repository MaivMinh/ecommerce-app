package com.minh.order_service.service;

import com.minh.common.events.CreatedOrderConfirmedEvent;
import com.minh.common.events.OrderCreatedEvent;
import com.minh.common.events.OrderCreatedRollbackedEvent;
import com.minh.common.response.ResponseData;
import com.minh.order_service.payload.response.OrderDetailRes;
import com.minh.order_service.query.controller.SearchOrdersRequest;
import com.minh.order_service.query.queries.FindOverallOrderStatusQuery;
import com.minh.order_service.query.queries.FindOverallStatusOfCreatingOrderQuery;
import com.minh.order_service.query.queries.GetOrderDetailQuery;
import com.minh.order_service.query.queries.SearchOrdersForUserQuery;

public interface OrderService {
    void createOrder(OrderCreatedEvent event);

    void rollbackOrderCreated(OrderCreatedRollbackedEvent event);

    /**
     * Hàm thực hiện xác thực đơn hàng sau khi hoàn thành toàn bộ quá trình đặt hàng.
     */
    void confirmCreatedOrder(CreatedOrderConfirmedEvent event);

    ResponseData findOverallStatusOfCreatingOrder(FindOverallStatusOfCreatingOrderQuery query);

    OrderDetailRes getOrderDetail(GetOrderDetailQuery query);

    ResponseData searchOrders(SearchOrdersRequest request);

    ResponseData searchOrdersForUser(SearchOrdersForUserQuery query);

    ResponseData findOverallOrderStatusQuery(FindOverallOrderStatusQuery query);
}
