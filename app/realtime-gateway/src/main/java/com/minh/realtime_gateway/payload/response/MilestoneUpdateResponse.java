package com.minh.realtime_gateway.payload.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MilestoneUpdateResponse {
    private String type;
    private String eventId;
    private String milestoneCode;
    private String username;
    private String description;
}