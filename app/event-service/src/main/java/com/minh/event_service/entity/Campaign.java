package com.minh.event_service.entity;

import com.minh.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Table(name = "campaigns")
@Entity
public class Campaign extends BaseEntity {
    @Id
    private String id;
    private String gameId;
    private String name;
    private Instant startTime;
    private Instant endTime;
}