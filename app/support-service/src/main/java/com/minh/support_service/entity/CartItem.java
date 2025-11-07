package com.minh.support_service.entity;

import com.minh.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cart_items")
public class CartItem extends BaseEntity {
    @Id
    private String id;
    private String cartId;
    private String productVariantId;
    private Integer quantity;
}
