package com.minh.realtime_gateway.DTOs;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RealtimeEvent {
    private String type;
    private String eventId;
    private Long executeAt;
    private JsonNode payload;
    private JsonNode vouchers;
    private Integer participants;

    @Override
    public String toString() {
        return "RealtimeEvent{" +
                "type='" + type + '\'' +
                ", eventId='" + eventId + '\'' +
                ", executeAt=" + executeAt +
                ", payload=" + payload +
                ", vouchers=" + vouchers +
                '}';
    }
}
