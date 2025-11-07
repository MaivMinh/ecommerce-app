package com.minh.support_service.enums;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("Nam"),
    FEMALE("Nữ"),
    OTHER("Khác");

    private final String description;

    Gender(String description) {
        this.description = description;
    }

}
