package com.minh.event_service.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherResponse {
    private String id;
    private String campaignId;
    private String code;
    private Double discountPercentage;
    private Double value;
    private Double maxValue;
    private String expirationDate;
}
