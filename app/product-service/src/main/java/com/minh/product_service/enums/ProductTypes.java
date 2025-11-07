package com.minh.product_service.enums;

import lombok.Getter;

@Getter
public enum ProductTypes {
    PRODUCT("product", "Sản phẩm"),
    SERVICE("service", "Dịch vụ");

    private final String code;
    private final String name;

    ProductTypes(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
