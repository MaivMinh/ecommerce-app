package com.minh.order_service.query.entity;

import com.minh.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItem extends BaseEntity {
    @Id
    private String id;
    private String orderId;
    private String productVariantId;
    private Integer quantity;
    private Double price;
    private Double total;
}
