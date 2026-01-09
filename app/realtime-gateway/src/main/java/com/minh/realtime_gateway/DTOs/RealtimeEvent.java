package com.minh.realtime_gateway.DTOs;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RealtimeEvent {
    private String type;
    private String eventId;
    private Long executeAt;
    private Map<String, Object> payload;
}
