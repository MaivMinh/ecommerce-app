package com.minh.realtime_gateway.DTOs;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WsMessage {
    private String type;
    private String eventId;
    private String clientTime;
    private String questionId;
    private String answer;

    @Override
    public String toString() {
        return "WsMessage{" +
                "type='" + type + '\'' +
                ", eventId='" + eventId + '\'' +
                ", clientTime='" + clientTime + '\'' +
                ", questionId='" + questionId + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}