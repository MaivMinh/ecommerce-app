package com.minh.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BusinessException extends RuntimeException implements Serializable {
    private Integer status;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)  /// Exclude null data from JSON serialization
    private Object data;

    public BusinessException(Integer status, String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }

    public BusinessException(Integer status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
