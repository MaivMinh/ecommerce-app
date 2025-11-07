package com.minh.order_service.command.controller;

import com.minh.common.commands.ReserveProductCommand;
import com.minh.common.commands.RollbackCreateOrderCommand;
import com.minh.common.constants.ResponseMessages;
import com.minh.common.response.ResponseData;
import com.minh.common.utils.AppUtils;
import com.minh.order_service.command.commands.CreateOrderCommand;
import com.minh.order_service.payload.request.CreateOrderRequest;
import com.minh.order_service.query.queries.FindOverallOrderStatusQuery;
import com.minh.order_service.query.queries.FindOverallStatusOfCreatingOrderQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@RequestMapping(value = "/api/orders")
@RequiredArgsConstructor
@Validated
@RestController
public class OrderCommandController {
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @PostMapping(value = "")
    public ResponseEntity<ResponseData> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        CreateOrderCommand command = CreateOrderCommand.builder()
                .orderId(AppUtils.generateUUIDv7())
                .username(AppUtils.getUsername())
                .shippingAddressId(request.getShippingAddressId())
                .currency(request.getCurrency())
                .discount(request.getDiscount())
                .total(request.getTotal())
                .subTotal(request.getSubTotal())
                .note(request.getNote())
                .paymentMethodId(request.getPaymentMethodId())
                .orderItemDtos(request.getOrderItemDtos())
                .promotionId(request.getPromotionId())
                .orderItemDtos(request.getOrderItemDtos())
                .productId(request.getProductId())
                .build();

        try (SubscriptionQueryResult<ResponseData, ResponseData> queryResult = queryGateway.subscriptionQuery(new FindOverallOrderStatusQuery(command.getOrderId()), ResponseData.class, ResponseData.class);) {
            commandGateway.send(command, new CommandCallback<>() {
                @Override
                public void onResult(@Nonnull CommandMessage<? extends CreateOrderCommand> commandMessage, @Nonnull CommandResultMessage<?> commandResultMessage) {
                    if (commandResultMessage.isExceptional()) {
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .body(ResponseData.builder()
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                        .message(ResponseMessages.INTERNAL_SERVER_ERROR)
                                        .data(null)
                                        .build());
                    }
                }
            });
            // Store the first update to avoid multiple blocking calls
            ResponseData responseData = queryResult.updates().blockFirst();
            if (responseData == null) {
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                        .body(ResponseData.builder()
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .message(ResponseMessages.INTERNAL_SERVER_ERROR)
                                .data(null)
                                .build());
            }
            return ResponseEntity.status(responseData.getStatus()).body(responseData);
        }
    }
}