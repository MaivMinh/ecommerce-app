package com.minh.payment_service.enums;

public enum PaymentProvider {
    PAYPAL("PAYPAL"),
    VIETTEL_MONEY("VIETTEL_MONEY"),
    MOMO("MOMO"),
    VNPAY("VNPAY"),
    TECHCOMBANK("TECHCOMBANK");

    private final String description;

    public String getDescription() {
        return description;
    }

    PaymentProvider(String description) {
        this.description = description;
    }
}
