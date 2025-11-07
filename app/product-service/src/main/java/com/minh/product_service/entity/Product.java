package com.minh.product_service.entity;

import com.minh.common.entity.BaseEntity;
import com.minh.common.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Product extends BaseEntity {
    @Id
    private String id;
    private String name;
    private String slug;
    private String categoryId;
    private String description;
    private String cover;
    private Double price;
    @Column(name = "original_price")
    private Double originalPrice;
    @Column(name = "sold_items")
    private Long soldItems;
    private Double rating;
    @Column(name = "is_featured")
    private Boolean isFeatured;
    @Column(name = "is_new")
    private Boolean isNew;
    @Column(name = "is_bestseller")
    private Boolean isBestseller;
    @Enumerated(value = EnumType.STRING)
    private ProductStatus status;
}
