package com.minh.order_service.entity;

import com.minh.common.entity.BaseEntity;
import com.minh.common.enums.MessageType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "outbox_messages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OutboxMessage extends BaseEntity {
    @Id
    private String id;
    private String messageId;
    @Enumerated(EnumType.STRING)
    private MessageType type;
    private String payload;
    private String topic;
    private Boolean processed;
    private Instant processedAt;
    private String className;
}