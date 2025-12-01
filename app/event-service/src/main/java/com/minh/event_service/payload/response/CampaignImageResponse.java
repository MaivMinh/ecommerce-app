package com.minh.event_service.payload.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CampaignImageResponse {
    private String id;
    private String campaignId;
    private String imageUrl;
}
