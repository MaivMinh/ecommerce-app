package com.minh.event_service.payload.request;

import com.google.type.DateTime;
import com.minh.common.DTOs.SearchDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchVouchersRequest extends SearchDTO {
    private String campaignId;
    private String code;
    private DateTime fromExpirationDate;
    private DateTime toExpirationDate;
}