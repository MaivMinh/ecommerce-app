package com.minh.payment_service.enums;

public enum PaymentProvider {
    VIETTEL_MONEY("VIETTEL_MONEY"),
    MOMO("MOMO"),
    VNPAY("VNPAY"),
    ZALO_PAY("ZALO_PAY");

    private final String description;

    public String getDescription() {
        return description;
    }

    PaymentProvider(String description) {
        this.description = description;
    }
}
