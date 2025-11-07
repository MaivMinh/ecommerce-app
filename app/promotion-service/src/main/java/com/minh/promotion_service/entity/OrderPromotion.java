package com.minh.promotion_service.entity;

import com.minh.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "order_promotions")
@Getter
@Setter
public class OrderPromotion extends BaseEntity {
    @Id
    private String id;
    private String orderId;
    private String promotionId;
    private Boolean isApplied;
    private Timestamp appliedAt;
}
