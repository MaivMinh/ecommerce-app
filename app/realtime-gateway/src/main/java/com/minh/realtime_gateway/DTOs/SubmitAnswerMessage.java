package com.minh.realtime_gateway.DTOs;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmitAnswerMessage {
    private String type;
    private String username;
    private String questionId;
    private Object payload;
}