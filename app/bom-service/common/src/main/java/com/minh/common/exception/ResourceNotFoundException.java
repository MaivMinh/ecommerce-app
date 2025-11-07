package com.minh.common.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final int status;

    public ResourceNotFoundException(int status, String message) {
        super(message);
        this.status = status;
    }
}