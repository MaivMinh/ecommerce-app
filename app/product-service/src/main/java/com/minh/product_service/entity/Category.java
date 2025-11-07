package com.minh.product_service.entity;

import com.minh.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "categories")
public class Category extends BaseEntity {
    @Id
    private String id;

    private String parentId;

    private String name;

    private String description;

    private String slug;

    private String image;
}
