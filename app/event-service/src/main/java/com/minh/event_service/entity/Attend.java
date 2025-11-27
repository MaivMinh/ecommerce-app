package com.minh.event_service.entity;

import com.minh.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "attends")
@Getter
@Setter
public class Attend extends BaseEntity {
    @Id
    private String id;
    private String username;
    private String campaignId;
    private String nickname;
    private Integer points;
}