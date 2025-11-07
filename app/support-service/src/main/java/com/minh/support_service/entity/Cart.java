package com.minh.support_service.entity;

import com.minh.common.entity.BaseEntity;
import jakarta.persistence.Column;
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
@Table(name = "carts")
public class Cart extends BaseEntity {
    @Id
    private String id;

    @Column(name = "username")
    private String username;
}
