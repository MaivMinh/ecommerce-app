package com.minh.promotion_service.enums;

import lombok.Getter;

@Getter
public enum PromotionType {
    percentage("Phần trăm"),

    fixed("Số tiền cố định"),

    shipping("Miễn phí vận chuyển"),

    buyOneGetOne("Mua 1 tặng 1");


    private final String description;

    PromotionType(String description) {
        this.description = description;
    }
}
