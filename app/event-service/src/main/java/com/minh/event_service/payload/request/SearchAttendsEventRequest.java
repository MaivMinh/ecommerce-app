package com.minh.event_service.payload.request;

import com.minh.common.DTOs.SearchDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchAttendsEventRequest extends SearchDTO {
    private String username;
    private String campaignId;
}
