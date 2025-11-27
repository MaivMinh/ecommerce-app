package com.minh.event_service.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
public class CreateCampaignRequest {
    private String gameId;
    private String name;
    private Instant startTime;
    private Instant endTime;
}
