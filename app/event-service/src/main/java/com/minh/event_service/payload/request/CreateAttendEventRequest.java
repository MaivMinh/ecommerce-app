package com.minh.event_service.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAttendEventRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String campaignId;
    private String nickname;
    private Integer points;
}