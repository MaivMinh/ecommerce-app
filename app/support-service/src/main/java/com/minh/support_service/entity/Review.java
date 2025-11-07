package com.minh.support_service.entity;

import com.minh.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reviews")
@Getter
@Setter
public class Review extends BaseEntity {
    @Id
    private String id;
    private String productId;
    private String username;
    private Integer rating;
    private String content;
    private Integer likes;
}
