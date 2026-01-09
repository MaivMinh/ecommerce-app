package com.minh.event_service.entity;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimelineEvent {
    private String type;
    private String eventId;
    private Long executeAt;
    private Map<String, Object> payload;
}
