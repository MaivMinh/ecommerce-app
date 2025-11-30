package com.minh.event_service.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionCollectionResponse {
    private String id;
    private String title;
    private Integer questionCount;
    private List<QuestionResponse> questions;
}
