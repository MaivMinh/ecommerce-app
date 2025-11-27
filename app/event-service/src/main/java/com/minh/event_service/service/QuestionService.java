package com.minh.event_service.service;

import com.minh.event_service.payload.request.CreateQuestionsCollectionRequest;
import jakarta.validation.Valid;

public interface QuestionService {
    void createQuestionsCollection(@Valid CreateQuestionsCollectionRequest request);
}
