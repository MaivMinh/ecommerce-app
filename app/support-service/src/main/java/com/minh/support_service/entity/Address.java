package com.minh.support_service.entity;

import com.minh.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "addresses")
@Entity
@Getter
@Setter
public class Address extends BaseEntity {
    @Id
    private String id;
    private String username;
    private String fullName;
    private String phone;
    private String address;
    private Boolean isDefault;
}
