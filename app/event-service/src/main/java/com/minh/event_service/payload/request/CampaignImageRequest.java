package com.minh.event_service.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CampaignImageRequest {
    private String id;
    private String imageUrl;
    private String campaignId;
}