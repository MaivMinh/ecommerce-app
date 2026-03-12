package com.minh.order_service.payload.request;

import com.minh.common.DTOs.OrderItemCreatedRequest;
import com.minh.common.events.SagaEvent;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateOrderRequest extends SagaEvent {
    @NotBlank
    private String shippingAddressId;
    @NotNull
    @Positive
    private Double subTotal;
    private Double discount;
    @NotNull
    @Positive
    private Double total;
    @NotBlank
    private String paymentMethod;
    private String promotionId;
    private String voucherId;
    private String currency;
    private String note;

    @NotNull
    @NotEmpty
    @Valid
    private List<OrderItemCreatedRequest> orderItemDtos;
    @NotBlank
    private String productId;

    @Override
    public void setSagaId(String sagaId) {
        super.setSagaId(sagaId);
    }
}