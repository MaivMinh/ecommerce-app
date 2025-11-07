package com.minh.promotion_service.enums;


import lombok.Getter;

@Getter
public enum PromotionStatus {
    active("Kích hoạt"),
    inactive("Vô hiệu");

    private String description;

    PromotionStatus(String description) {
        this.description = description;
    }
}

