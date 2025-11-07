package com.minh.support_service.entity;

import com.minh.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "purchased_products")
@Entity
@Getter
@Setter
public class PurchasedProduct extends BaseEntity {
    @Id
    private String id;
    private String productId;
    private String username;
}