package com.minh.event_service.payload.response;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CampaignResponse {
    private String id;
    private String gameId;
    private String name;
    private String startTime;
    private String endTime;
    private String questionCollectionId;
    private List<VoucherResponse> vouchers;
}
