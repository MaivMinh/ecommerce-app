package com.minh.order_service.enums;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;

public enum NotifyTemplateCode {
    ORDER_CONFIRMATION,
    PAYMENT_SUCCESS,
    ORDER_DELIVERED,
    ORDER_FAILED,
    ORDER_CANCELLED,
    ORDER_SUCCESS;
}
