package com.minh.event_service.service.impl;

import com.minh.common.utils.AppUtils;
import com.minh.event_service.entity.Question;
import com.minh.event_service.entity.QuestionCollection;
import com.minh.event_service.payload.request.CreateQuestionsCollectionRequest;
import com.minh.event_service.repository.QuestionCollectionRepository;
import com.minh.event_service.repository.QuestionRepository;
import com.minh.event_service.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionCollectionRepository questionCollectionRepository;
    private final ModelMapper modelMapper;
    private final QuestionRepository questionRepository;

    @Override
    public void createQuestionsCollection(CreateQuestionsCollectionRequest request) {
        if (!StringUtils.hasText(request.getTitle())) {
            throw new IllegalArgumentException("Vui lòng cung cấp tiêu đề cho bộ câu hỏi!");
        }

        QuestionCollection collection = new QuestionCollection();
        collection.setId(AppUtils.generateUUIDv7());
        collection.setTitle(request.getTitle());
        QuestionCollection saved = questionCollectionRepository.save(collection);
        // Lưu các câu hỏi vào bộ câu hỏi đã tạo

        List<Question> questions = request.getQuestions()
                .stream().map(req -> {
                    Question question = new Question();
                    modelMapper.map(req, question);
                    question.setId(AppUtils.generateUUIDv7());
                    question.setCollectionId(saved.getId());
                    return question;
                }).toList();
        questionRepository.saveAll(questions);
    }
}
