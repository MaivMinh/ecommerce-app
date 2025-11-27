package com.minh.event_service.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionRequest {
    private String content;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption;
}
