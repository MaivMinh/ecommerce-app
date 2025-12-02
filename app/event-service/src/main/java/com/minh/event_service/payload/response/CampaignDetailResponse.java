package com.minh.event_service.payload.response;


import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class CampaignDetailResponse {
    private String id;
    private GameResponse game;
    private String name;
    private Instant startTime;
    private Instant endTime;
    private String questionCollectionName;
    private List<VoucherResponse> vouchers;
    private List<CampaignImageResponse> campaignImages;
    private Boolean isRegistered;
}
