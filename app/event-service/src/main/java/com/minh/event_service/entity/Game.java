package com.minh.event_service.entity;

import com.minh.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "games")
@Entity
@Getter
@Setter
public class Game extends BaseEntity {
    @Id
    private String id;
    private String name;
}
