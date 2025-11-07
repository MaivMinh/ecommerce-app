package com.minh.product_service.entity;

import com.minh.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_variants")
public class ProductVariant extends BaseEntity {
    @Id
    private String id;
    private String productId;
    private String size;
    private String colorName;
    private String colorHex;
    private Double price;
    private Double originalPrice;
    private Integer quantity;
}
