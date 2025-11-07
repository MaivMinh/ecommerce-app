package com.minh.order_service.query.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minh.common.entity.BaseEntity;
import com.minh.order_service.enums.OrderStatus;
import io.swagger.v3.core.util.Json;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity {
    @Id
    private String id;
    private String username;
    private String shippingAddressId;
    private Double subTotal;
    private Double discount;
    private Double total;
    private String currency;
    private String note;
    @Enumerated(value = jakarta.persistence.EnumType.STRING)
    private OrderStatus status;

    public Timestamp getCreatedAt() {
        return super.createdAt;
    }

    public String getCreatedBy() {
        return super.createdBy;
    }
}
