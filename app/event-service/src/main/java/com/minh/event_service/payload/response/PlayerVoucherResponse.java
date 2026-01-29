package com.minh.event_service.payload.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerVoucherResponse {
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
