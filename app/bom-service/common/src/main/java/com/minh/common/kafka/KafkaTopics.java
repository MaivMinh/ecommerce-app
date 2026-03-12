package com.minh.common.kafka;

public final class KafkaTopics {


    public static final String NOTIFY_ORDER_CANCELLED = "notify.order.cancelled";
    public static final String PAYMENT_REFUND = "payment.refund";

    private KafkaTopics() {}

    // Order Saga Topics
    public static final String ORDER_CREATED = "order.created";
    public static final String ORDER_COMPLETED = "order.completed";
    public static final String CANCELLED = "order.cancelled";
    public static final String ORDER_COMPLETION_FAILED = "order.cancelled";

    // Payment Topics
    public static final String PAYMENT_PROCESS = "payment.process";
    public static final String PAYMENT_PROCESSED = "payment.processed";
    public static final String PAYMENT_REFUNDED = "payment.refunded";

    // PRODUCT/Product Topics
    public static final String PRODUCT_RESERVED_CONFIRMING = "product.reserve.confirming";
    public static final String PRODUCT_RESERVED_CONFIRMED = "product.reserve.confirmed";
    public static final String PRODUCT_RESERVE = "product.reserve";
    public static final String PRODUCT_RESERVED = "product.reserved";
    public static final String PRODUCT_RELEASE = "product.release";
    public static final String PRODUCT_RELEASED = "product.released";
    public static final String PRODUCT_RESERVATION_FAILED = "product.insufficient";
}
