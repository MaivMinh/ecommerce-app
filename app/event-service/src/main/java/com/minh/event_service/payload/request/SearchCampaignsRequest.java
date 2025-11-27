package com.minh.event_service.payload.request;

import com.minh.common.DTOs.SearchDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class SearchCampaignsRequest extends SearchDTO {
    private String gameId;
    private String name;
    private Instant fromStartTime;
    private Instant toStartTime;
    private Instant fromEndTime;
    private Instant toEndTime;
}
