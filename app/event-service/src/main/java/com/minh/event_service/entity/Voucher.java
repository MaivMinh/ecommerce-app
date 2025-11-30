package com.minh.event_service.entity;

import com.google.type.DateTime;
import com.minh.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Table(name = "vouchers")
@Entity
@Getter
@Setter
public class Voucher extends BaseEntity {
    @Id
    private String id;
    private String campaignId;
    private String code;
    private BigDecimal discountPercentage;
    private BigDecimal value;
    private BigDecimal maxValue;
    private Instant expirationDate;
    private Integer voucherOrder;
}
