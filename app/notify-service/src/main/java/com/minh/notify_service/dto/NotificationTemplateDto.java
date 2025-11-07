package com.minh.notify_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationTemplateDto {
    private String id;
    private String templateCode;
    private String channel; // e.g., EMAIL, SMS
    private String title;
    private String content;
    private String params;
    private Boolean isActive;
}
