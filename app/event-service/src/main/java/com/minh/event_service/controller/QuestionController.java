package com.minh.event_service.controller;

import com.minh.common.constants.ResponseMessages;
import com.minh.common.response.ResponseData;
import com.minh.event_service.payload.request.CreateQuestionsCollectionRequest;
import com.minh.event_service.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/questions")
@Validated
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping(value = "")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> createQuestionsCollection(@RequestBody @Valid CreateQuestionsCollectionRequest request) {
        questionService.createQuestionsCollection(request);

        return ResponseEntity.ok(ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(null)
                .build()
        );
    }


}
