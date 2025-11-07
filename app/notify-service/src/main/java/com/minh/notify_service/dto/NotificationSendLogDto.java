package com.minh.notify_service.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSendLogDto {
    private String id;
    private String templateCode;
    private String params;
    private String recipient; // e.g., email address or phone number
    private String renderedTitle;
    private String renderedContent;
    private String status; // e.g., SENT, FAILED, PENDING
    private Integer attempts;
    private String lastError;
    private Timestamp sentAt;
}
