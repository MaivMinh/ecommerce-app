package com.minh.event_service.controller;

import com.minh.common.constants.ResponseMessages;
import com.minh.common.response.ResponseData;
import com.minh.event_service.payload.request.SearchQuestionCollectionRequest;
import com.minh.event_service.payload.request.UpdateQuestionsCollectionRequest;
import com.minh.event_service.payload.request.CreateQuestionsCollectionRequest;
import com.minh.event_service.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/question-collections")
@Validated
public class QuestionCollectionController {
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

    @PutMapping(value = "")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> updateQuestionsCollection(@RequestBody @Valid UpdateQuestionsCollectionRequest request) {
        questionService.updateQuestionCollections(request);

        return ResponseEntity.ok(ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(null)
                .build()
        );
    }


    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> deleteQuestionsCollection(@PathVariable("id") String id)    {
        questionService.deleteQuestionsCollection(id);

        return ResponseEntity.ok(ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(null)
                .build()
        );
    }

    @GetMapping(value = "")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> getAllQuestionCollections() {
        ResponseData response = questionService.getAllQuestionCollections();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ResponseData> getQuestionCollectionById(@PathVariable("id") String id) {
        ResponseData response = questionService.getQuestionCollectionById(id);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(value = "/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> searchQuestionCollections(@RequestBody SearchQuestionCollectionRequest request) {
        ResponseData response = questionService.searchQuestionCollections(request);

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}