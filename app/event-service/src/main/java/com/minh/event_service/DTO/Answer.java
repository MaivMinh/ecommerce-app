package com.minh.event_service.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Answer {
    private Long answerId;
    private Boolean correct;
    private String answerText;
}