package com.minh.notify_service.entity;

import com.minh.common.entity.BaseEntity;
import com.minh.notify_service.enums.TemplateChannel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "notification_templates")
@Getter
@Setter
@Entity
public class NotificationTemplate extends BaseEntity {
    @Id
    private String id;
    private String templateCode;
    @Enumerated(EnumType.STRING)
    private TemplateChannel channel; // e.g., EMAIL, SMS
    private String title;
    private String content;
    @Column(columnDefinition = "JSON")
    private String params;
    private Boolean isActive;
}
