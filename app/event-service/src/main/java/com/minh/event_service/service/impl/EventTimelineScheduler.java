package com.minh.event_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.event_service.entity.Campaign;
import com.minh.event_service.entity.TimelineEvent;
import com.minh.event_service.enums.GameEventType;
import com.minh.event_service.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventTimelineScheduler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String TIMELINE_KEY = "event:timeline";
    private final CampaignRepository campaignRepository;

    public void scheduleCampaign(Campaign campaign) {
        /// Thực hiện xóa toàn bộ các event timeline cũ của campaign này nếu có.
        redisTemplate.opsForZSet().removeRange(TIMELINE_KEY,0,-1);

        String campaignId = campaign.getId();
        long start = campaign.getStartTime().toEpochMilli();
        int totalQuestions = campaignRepository.getTotalQuestionsByCampaignId(campaignId);

        // T-30s
        addEvent(GameEventType.GAME_READY, campaignId, start - 30000L, Map.of("seconds", 30));

        // T0
        addEvent(GameEventType.GAME_START, campaignId, start - 50000L, Map.of("seconds", 5));

        // Questions
        for (int i = 0; i < totalQuestions; i++) {
            addEvent(
                    GameEventType.QUESTION,
                    campaignId,
                    start + i * 30000L,
                    Map.of("index", i)
            );
        }

        // Scoring
        addEvent(
                GameEventType.SCORING,
                campaignId,
                start + totalQuestions * 30000L,
                null
        );

        // Result
        addEvent(
                GameEventType.GAME_RESULT,
                campaignId,
                start + totalQuestions * 30000L + 30000,
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