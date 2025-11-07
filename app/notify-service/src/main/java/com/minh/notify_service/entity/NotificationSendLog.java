package com.minh.notify_service.entity;

import com.minh.common.entity.BaseEntity;
import com.minh.notify_service.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Table(name = "notification_send_logs")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSendLog extends BaseEntity {
    @Id
    private String id;
    private String templateCode;
    private String params;
    private String recipient; // e.g., email address or phone number
    private String renderedTitle;
    private String renderedContent;
    @Enumerated(EnumType.STRING)
    private NotificationStatus status; // e.g., SENT, FAILED, PENDING
    private Integer attempts;
    private String lastError;
    private LocalDateTime sentAt;
}
