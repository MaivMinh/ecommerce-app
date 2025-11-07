package com.minh.payment_service.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeletePaymentMethodRequest {
    private String id;
    private Long paymentMethodId;
}
