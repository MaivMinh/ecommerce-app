package com.minh.event_service.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VoucherRedeemedResponse {
    private String id;
    private String code;
    private String type;
    private BigDecimal discountValue;
    private String startDate;
    private String endDate;
    private BigDecimal maxValue;
    private String status;
}