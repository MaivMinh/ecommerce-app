package com.minh.common.constants;

public class ErrorCode {
    public static final String INTERNAL_SERVER_ERROR = "internal.server.error";
    public static final String INVALID_PARAMS = "invalid.params";
    public static final String INVALID_REQUEST = "invalid.request";
    public static final String QUERY_EXECUTION_ERROR = "query.execution.error";
    public static final String FORBIDDEN = "forbidden";

    public static class Category {
        public static final String NOT_FOUND = "category.not.found";
        public static final String SLUG_NOT_FOUND = "category.slug.not.found";
    }

    public static class Product {
        public static final String NOT_FOUND = "product.not.found";
        public static final String SLUG_NOT_FOUND = "product.slug.not.found";
    }

    public static class ProductVariant {
        public static final String NOT_FOUND = "product.variant.not.found";
        public static final String INSUFFICIENT_QUANTITY = "product.variant.insufficient.quantity";
    }

    public static class Cart {
        public static final String NOT_FOUND_WITH_USER_ID = "cart.not.found.with.user.id";
    }

    public static class CartItem {
        public static final String NOT_FOUND = "cart.item.not.found";
    }

    public static class User {
        public static final String NOT_FOUND = "user.not.found";
        public static final String USERNAME_NOT_FOUND = "username.not.found";
        public static final String USERNAME_EXISTED = "user.username.existed";
        public static final String EMAIL_EXISTED = "user.email.existed";
    }

    public static class Address {
        public static final String NOT_FOUND = "address.not.found";
    }

    public static class Auth {
        public static final String UNAUTHORIZED = "unauthorized";
        public static final String FORBIDDEN = "forbidden";
        public static final String INVALID_CREDENTIALS = "invalid.credentials";
        public static final String EXPIRED_TOKEN = "expired.token";
        public static final String INVALID_TOKEN = "invalid.token";
    }

    public static class PaymentMethod {
        public static final String NOT_FOUND = "payment.method.not.found";
    }

    public static class Promotion {
        public static final String NOT_FOUND = "promotion.not.found";
        public static final String QUANTITY_USAGE_LIMITED = "promotion.quantity.usage.limited";
    }

    public static class Order {
        public static final String NOT_FOUND = "order.not.found";
    }
    public static class Payment {
        public static final String NOT_FOUND = "payment.not.found";
        public static final String PAYMENT_FAILED = "payment.failed";
    }

    public static class OrderPromotion {
        public static final String NOT_FOUND = "order.promotion.not.found";
    }

    public static class Review {
        public static final String NOT_FOUND = "review.not.found";
    }

    public static class NOTIFICATION_TEMPLATE {
        public static final String TEMPLATE_CODE_EXISTED = "notification.template.template.code.existed";
        public static final String NOT_FOUND = "notification.template.not.found";
    }
}
