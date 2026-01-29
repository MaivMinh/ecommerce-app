package com.minh.event_service.entity;

import com.minh.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;

@Table(name = "player_vouchers")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerVoucher extends BaseEntity {
    @Id
    private String id;
    private String username;
    private String voucherId;
    private String code;
    private String campaignId;
    private Instant redeemedAt;
    private Double discountPercentage;
    private Double value;
    private Double maxValue;
    private Boolean used;
}
