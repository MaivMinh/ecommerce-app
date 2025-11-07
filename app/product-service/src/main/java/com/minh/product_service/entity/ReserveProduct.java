package com.minh.product_service.entity;

import com.minh.common.entity.BaseEntity;
import com.minh.product_service.enums.ReserveProductStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Table(name = "reserve_products")
@Entity
@Getter
@Setter
public class ReserveProduct extends BaseEntity {
    @Id
    private String id;
    private String productVariantId;
    private String orderId;
    private Integer quantity;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private ReserveProductStatus status;
}