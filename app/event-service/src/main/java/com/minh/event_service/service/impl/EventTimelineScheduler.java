package com.minh.event_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.event_service.DTO.Answer;
import com.minh.event_service.entity.Campaign;
import com.minh.event_service.entity.Question;
import com.minh.event_service.entity.TimelineEvent;
import com.minh.common.enums.GameEventType;
import com.minh.event_service.repository.CampaignRepository;
import com.minh.event_service.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventTimelineScheduler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String TIMELINE_KEY = "event:timeline";
    private final CampaignRepository campaignRepository;
    private final QuestionRepository questionRepository;

    public void scheduleCampaign(Campaign campaign) {
        /// Thực hiện xóa toàn bộ các event timeline cũ của campaign này nếu có.
        redisTemplate.opsForZSet().removeRange(TIMELINE_KEY, 0, -1);

        String campaignId = campaign.getId();
        long start = campaign.getStartTime().toEpochMilli();
        int totalQuestions = campaignRepository.getTotalQuestionsByCampaignId(campaignId);

        // T-30s
        addEvent(GameEventType.GAME_READY, campaignId, start - 30000L, Map.of("seconds", GameEventType.GAME_READY.getValue(), "description", GameEventType.GAME_READY.getDescription()));

        /// Lấy danh sách toàn bộ câu hỏi của Event.
        List<Question> questions = questionRepository.findAllQuestionsRelatedToCampaignId(campaignId);
        // T0
        addEvent(GameEventType.GAME_START, campaignId, start - 5000L, Map.of("seconds", GameEventType.GAME_START.getValue(), "description", GameEventType.GAME_START.getDescription(), "totalQuestions", totalQuestions));

        // Questions
        for (int i = 0; i < totalQuestions; i++) {
            Question question = questions.get(i);
            List<Answer> answers = new ArrayList<>();
            answers.add(Answer.builder()
                            .answerId(1L)
                            .answerText(question.getOptionA())
                            .correct(question.getCorrectOption().equals(question.getOptionA()) ? Boolean.TRUE: Boolean.FALSE)
                    .build());

            answers.add(Answer.builder()
                    .answerId(2L)
                    .answerText(question.getOptionB())
                    .correct(question.getCorrectOption().equals(question.getOptionB()) ? Boolean.TRUE: Boolean.FALSE)
                    .build());

            answers.add(Answer.builder()
                    .answerId(3L)
                    .answerText(question.getOptionC())
                    .correct(question.getCorrectOption().equals(question.getOptionC()) ? Boolean.TRUE: Boolean.FALSE)
                    .build());

            answers.add(Answer.builder()
                    .answerId(4L)
                    .answerText(question.getOptionD())
                    .correct(question.getCorrectOption().equals(question.getOptionD()) ? Boolean.TRUE: Boolean.FALSE)
                    .build());


            addEvent(
                    GameEventType.QUESTION,
                    campaignId,
                    start + i * 10000L,
                    Map.of("index", i,
                            "question", questions.get(i),
                            "answers", answers,
                            "timeLimit", 10,
                            "score",questions.get(i).getScore()
                    )
            );
        }

        // Scoring
        addEvent(
                GameEventType.SCORING,
                campaignId,
                start + totalQuestions * 10000L,
                Map.of("seconds", GameEventType.SCORING.getValue(), "description", GameEventType.SCORING.getDescription())
        );

        // Result
        addEvent(
                GameEventType.GAME_RESULT,
                campaignId,
                start + totalQuestions * 10000L + 10000L,
                null
        );

        /// Cleanup all information related to this campaign after 20 seconds of game end.
        addEvent(
                GameEventType.CLEANUP,
                campaignId,
                start + totalQuestions * 10000L + 20000L,
                null
        );
    }

    private void addEvent(GameEventType type,
                          String eventId,
                          long executeAt,
                          Map<String, Object> payload) {

        TimelineEvent event = TimelineEvent.builder()
                .type(type.name())
                .eventId(eventId)
                .executeAt(executeAt)
                .payload(payload)
                .build();

        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.opsForZSet().add(TIMELINE_KEY, json, executeAt);
        } catch (Exception e) {
            throw new RuntimeException("Schedule event failed", e);
        }
    }
}