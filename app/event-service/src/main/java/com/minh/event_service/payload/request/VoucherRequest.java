package com.minh.event_service.payload.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class VoucherRequest {
    private String id;
    private String campaignId;
    private String code;
    private BigDecimal discountPercentage;
    private BigDecimal value;
    private BigDecimal maxValue;
    private Instant expirationDate;
    private Integer voucherOrder;
}