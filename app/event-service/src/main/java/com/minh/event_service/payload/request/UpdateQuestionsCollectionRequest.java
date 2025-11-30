package com.minh.event_service.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateQuestionsCollectionRequest {
    @NotBlank
    private String id;
    @NotBlank
    private String title;
    @NotNull
    @Size(min = 1, message = "Vui lòng cung cấp ít nhất một câu hỏi!")
    private List<QuestionRequest> questions;
}
