package com.minh.event_service.service;

import com.minh.common.response.ResponseData;
import com.minh.event_service.payload.request.SearchQuestionCollectionRequest;
import com.minh.event_service.payload.request.UpdateQuestionsCollectionRequest;
import com.minh.event_service.payload.request.CreateQuestionsCollectionRequest;
import jakarta.validation.Valid;

public interface QuestionService {
    void createQuestionsCollection(@Valid CreateQuestionsCollectionRequest request);

    /**
     * Hàm thực hiện cập nhật lại bộ câu hỏi.
     *
     * @param request Dữ liệu cập nhật bộ câu hỏi
     */
    void updateQuestionCollections(UpdateQuestionsCollectionRequest request);

    void deleteQuestionsCollection(String id);

    ResponseData getAllQuestionCollections();

    ResponseData getQuestionCollectionById(String id);

    /**
     * Tìm kiếm bộ câu hỏi dựa trên các tiêu chí trong request.
     *
     * @param request Dữ liệu tìm kiếm bộ câu hỏi
     * @return Kết quả tìm kiếm bộ câu hỏi
     */
    ResponseData searchQuestionCollections(SearchQuestionCollectionRequest request);
}
