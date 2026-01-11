package com.minh.event_service.service.impl;

import com.minh.event_service.DTO.UserScoreData;
import com.minh.event_service.DTO.WsMessage;
import com.minh.event_service.service.GameLogicHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GameLogicHandlerImpl implements GameLogicHandler {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void handlePlayAnswer(WsMessage event) {
        String userScoreKey = "event:" + event.getEventId() + ":scores";
        String username = event.getUsername();
        int score = event.getIsCorrect() ? 10 : 0;


        UserScoreData userData = (UserScoreData) redisTemplate.opsForHash().get(userScoreKey, username);
        if (Objects.isNull(userData)) {
            userData = UserScoreData.builder()
                    .username(username)
                    .score(score)
                    .correct(event.getIsCorrect() ? 1 : 0)
                    .build();
        } else {
            int currentScore = userData.getScore();
            int currentCorrect = userData.getCorrect();
            userData = UserScoreData.builder()
                    .username(username)
                    .score(currentScore + score)
                    .correct(currentCorrect + (event.getIsCorrect() ? 1 : 0))
                    .build();
        }
        redisTemplate.opsForHash().put(userScoreKey, username, userData);
    }


}
