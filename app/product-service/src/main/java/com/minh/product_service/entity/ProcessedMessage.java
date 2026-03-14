package com.minh.product_service.entity;

import com.minh.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "processed_messages")
public class ProcessedMessage extends BaseEntity {
    @Id
    private String id;
    private Instant processedAt;
}