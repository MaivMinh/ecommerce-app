package com.minh.realtime_gateway.DTOs;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WsMessage implements Serializable {
    private String type;
    private String eventId;
    private String clientTime;
    private String questionId;
    private Boolean isCorrect;
    private String username;
}