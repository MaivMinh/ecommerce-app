package com.minh.common.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder    /// If we use @Builder, we have to use @AllArgsConstructor. But, before using @AllArgsConstructor, we need to use default constructor @NoArgsConstructor.
public class ResponseData {
    @JsonIgnore
    private Integer status;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;
    private String timestamp;

    public ResponseData(String message) {
        this.status = 200;
        this.message = message;
        this.data = null;
        this.timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(java.time.LocalDateTime.now());
    }

    public ResponseData(Object payload) {
        this.status = 200;
        this.message = "Success";
        this.data = payload;
        this.timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(java.time.LocalDateTime.now());
    }

    public ResponseData(Integer status, String message, Object payload) {
        this.status = status;
        this.message = message;
        this.data = payload;
        this.timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(java.time.LocalDateTime.now());
    }
}
