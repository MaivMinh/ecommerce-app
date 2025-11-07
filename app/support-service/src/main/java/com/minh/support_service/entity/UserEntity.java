package com.minh.support_service.entity;

import com.minh.common.entity.BaseEntity;
import com.minh.support_service.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Table(name = "users")
@Entity
@Getter
@Setter
public class UserEntity extends BaseEntity {
    @Id
    private String id;
    private String username;
    private String fullName;
    private String avatar;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Date birthDate;
    private String emails;
    private String mobiles;
}
