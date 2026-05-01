package com.minh.event_service.service.impl;

import com.minh.common.constants.ErrorCode;
import com.minh.common.constants.ResponseMessages;
import com.minh.common.message.MessageCommon;
import com.minh.common.response.ResponseData;
import com.minh.common.utils.AppUtils;
import com.minh.event_service.entity.Question;
import com.minh.event_service.entity.QuestionCollection;
import com.minh.event_service.payload.request.SearchQuestionCollectionRequest;
import com.minh.event_service.payload.request.UpdateQuestionsCollectionRequest;
import com.minh.event_service.payload.request.CreateQuestionsCollectionRequest;
import com.minh.event_service.payload.request.QuestionRequest;
import com.minh.event_service.payload.response.QuestionCollectionResponse;
import com.minh.event_service.payload.response.QuestionResponse;
import com.minh.event_service.repository.QuestionCollectionRepository;
import com.minh.event_service.repository.QuestionRepository;
import com.minh.event_service.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionCollectionRepository questionCollectionRepository;
    private final ModelMapper modelMapper;
    private final QuestionRepository questionRepository;
    private final MessageCommon messageCommon;

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
                    question.setScore(10);
                    return question;
                }).toList();
        questionRepository.saveAll(questions);
    }

    @Override
    @Transactional
    public void updateQuestionCollections(UpdateQuestionsCollectionRequest request) {
        QuestionCollection collection = questionCollectionRepository.findById(request.getId()).orElseThrow(
                () -> new RuntimeException(messageCommon.getMessage(ErrorCode.QuestionCollection.NOT_FOUND, request.getId()))
        );

        if (StringUtils.hasText(request.getTitle())) {
            collection.setTitle(request.getTitle());
        }

        /// Cập nhật lại thông tin của từng câu hỏi trong bộ câu hỏi.
        List<Question> questions = questionRepository.findQuestionByQuestionCollectionId(collection.getId());

        /// Các câu hỏi được gửi từ phía Client sẽ có 2 dạng, một là đã tồn tại, 2 là câu hỏi mới.
        /// Nếu các câu hỏi đã tồn tại dưới DB nhưng không được gửi từ phía Client, thì sẽ bị xóa khỏi bộ câu hỏi.
        Map<String, Question> questionMap = questions.stream()
                .collect(java.util.stream.Collectors.toMap(Question::getId, q -> q));

        List<Question> updatedQuestions = new ArrayList<>();

        for (QuestionRequest question : request.getQuestions()) {
            if (StringUtils.hasText(question.getId())) {
                // Câu hỏi đã tồn tại, thực hiện cập nhật lại thông tin.
                Question existingQuestion = questionMap.get(question.getId());
                if (existingQuestion != null) {
                    modelMapper.map(question, existingQuestion);
                    existingQuestion.setCollectionId(collection.getId());
                    updatedQuestions.add(existingQuestion);
                    questionMap.remove(question.getId());   /// Nếu câu hỏi này tồn tại dưới DB thì xóa nó trong Map.
                } else {
                    throw new RuntimeException(messageCommon.getMessage(ErrorCode.Question.NOT_FOUND, question.getId()));
                }
            } else {
                // Câu hỏi mới, thực hiện tạo mới.
                Question newQuestion = new Question();
                modelMapper.map(question, newQuestion);
                newQuestion.setId(AppUtils.generateUUIDv7());
                newQuestion.setCollectionId(collection.getId());
                updatedQuestions.add(newQuestion);
            }
        }

        // Xóa các câu hỏi không còn trong bộ câu hỏi. Việc thực hiện xóa câu hỏi nếu như nó tồn tại trong DB khỏi Map, điều này giúp cho trong Map sẽ chỉ còn các câu hỏi không còn trong bộ câu hỏi được gửi từ phía Client.
        List<String> questionsToDelete = questionMap.values().stream()
                .map(Question::getId)
                .toList();

        if (!questionsToDelete.isEmpty()) {
            questionRepository.deleteAllById(questionsToDelete);
        }

        // Lưu lại tất cả các câu hỏi đã được cập nhật.
        questionRepository.saveAll(updatedQuestions);

        // Lưu lại thông tin bộ câu hỏi.
        questionCollectionRepository.save(collection);
    }

    @Override
    @Transactional
    public void deleteQuestionsCollection(String id) {
        QuestionCollection collection = questionCollectionRepository.findById(id).orElseThrow(
                () -> new RuntimeException(messageCommon.getMessage(ErrorCode.QuestionCollection.NOT_FOUND, id))
        );

        /// Xóa toàn bộ câu hỏi trong bộ sưu tập.
        questionRepository.deleteAllByCollectionId(collection.getId());
        /// Xóa bộ câu hỏi.
        questionCollectionRepository.delete(collection);
    }

    @Override
    public ResponseData getAllQuestionCollections() {
        List<QuestionCollection> collections = questionCollectionRepository.findAll();

        List<QuestionCollectionResponse> result = processQuestionCollection(collections);
        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(result)
                .build();
    }

    @Override
    public ResponseData getQuestionCollectionById(String id) {
        QuestionCollection collection = questionCollectionRepository.findById(id).orElseThrow(
                () -> new RuntimeException(messageCommon.getMessage(ErrorCode.QuestionCollection.NOT_FOUND, id))
        );

        List<Question> questions = questionRepository.findQuestionByQuestionCollectionId(collection.getId());
        QuestionCollectionResponse response = new QuestionCollectionResponse();
        modelMapper.map(collection, response);
        response.setQuestionCount(questions.size());
        List<QuestionResponse> questionResponses = questions.stream().map(
                question -> {
                    QuestionResponse questionResponse = new QuestionResponse();
                    modelMapper.map(question, questionResponse);
                    return questionResponse;
                }
        ).toList();
        response.setQuestions(questionResponses);
        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(response)
                .build();
    }

    @Override
    public ResponseData searchQuestionCollections(SearchQuestionCollectionRequest request) {
        Pageable pageable = AppUtils.toPageable(request);

        Page<QuestionCollection> collections = questionCollectionRepository.searchQuestionCollections(request, pageable);

        Page<QuestionCollectionResponse> result = collections.map(
                collection -> {
                    List<Question> questions = questionRepository.findQuestionByQuestionCollectionId(collection.getId());
                    QuestionCollectionResponse response = new QuestionCollectionResponse();
                    modelMapper.map(collection, response);
                    response.setQuestionCount(questions.size());
                    List<QuestionResponse> questionResponses = questions.stream().map(
                            question -> {
                                QuestionResponse questionResponse = new QuestionResponse();
                                modelMapper.map(question, questionResponse);
                                return questionResponse;
                            }
                    ).toList();
                    response.setQuestions(questionResponses);
                    return response;
                }
        );

        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(result)
                .build();
    }


    private List<QuestionCollectionResponse> processQuestionCollection(List<QuestionCollection> collections) {
        List<QuestionCollectionResponse> collectionsResponse = collections.stream().map(
                collection -> {
                    List<Question> questions = questionRepository.findQuestionByQuestionCollectionId(collection.getId());
                    QuestionCollectionResponse response = new QuestionCollectionResponse();
                    modelMapper.map(collection, response);
                    response.setQuestionCount(questions.size());
                    List<QuestionResponse> questionResponses = questions.stream().map(
                            question -> {
                                QuestionResponse questionResponse = new QuestionResponse();
                                modelMapper.map(question, questionResponse);
                                return questionResponse;
                            }
                    ).toList();
                    response.setQuestions(questionResponses);
                    return response;
                }
        ).toList();

        return collectionsResponse;
    }
}