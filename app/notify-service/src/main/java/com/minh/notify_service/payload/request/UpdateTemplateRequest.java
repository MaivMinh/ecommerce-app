package com.minh.notify_service.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class UpdateTemplateRequest {
    @NotBlank
    private String id;
    @NotBlank
    private String templateCode;
    @NotBlank
    private String channel; // e.g., EMAIL, SMS
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private String params;
    private Boolean isActive;
}
