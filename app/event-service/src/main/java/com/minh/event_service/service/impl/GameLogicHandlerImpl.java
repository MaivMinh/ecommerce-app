package com.minh.event_service.service.impl;

import com.minh.event_service.DTO.WsMessage;
import com.minh.event_service.service.GameLogicHandler;
import event_service.MilestoneResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameLogicHandlerImpl implements GameLogicHandler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Map<String, StreamObserver<MilestoneResponse>> observers = new HashMap<>();

    @Override
    public void handlePlayAnswer(WsMessage event) {

        String milestoneKey = null;
        if (Objects.isNull(event) || Objects.isNull(event.getIsCorrect()) || !event.getIsCorrect()) {
            /// Update milestone.
            milestoneKey = "event:" + event.getEventId() + ":milestone";    /// String.
            redisTemplate.opsForZSet().add(milestoneKey, event.getUsername(), 0);
            return;
        }
        log.info("Handling play answer for event: {}", event);

        String playerKey = "event:" + event.getEventId() + ":username:" + event.getUsername();  /// Hash Atomic.
        String rankingKey = "event:" + event.getEventId() + ":ranking";    /// Sorted Set.
        milestoneKey = "event:" + event.getEventId() + ":milestone";    /// String.

        /// 1. Update score & correct. Hash Atomic.
        /*
        * playerKey -> {
        *  "score": 100,
        *  "correct": 5
        * }
        * */
        redisTemplate.opsForHash().increment(playerKey, "score", 10);
        redisTemplate.opsForHash().increment(playerKey, "correct", 1);

        /// 2. Update ranking. Sorted Set.
        /// rankingKey: Đại diện cho một tập Set riêng biệt, kiểu như nếu có nhiều event thì mỗi event sẽ có một rankingKey riêng để lưu trữ điểm số của người chơi trong event đó.
        /// event.getUsername(): là một phần tử (element) trong tập Set rankingKey trên.
        /// Các username này nếu là Set thông thường thì sẽ không có thứ tự, nhưng vì là Sorted Set nên mỗi username (element) này sẽ đi kèm với một điểm số (Score) để xác định vị trí của nó trong tập Set.
        /// Và vì vậy, chúng ta có thể xem Sorted Set này giống với HashMap, trong đó key là username và value là điểm số (score) của người chơi đó.

        /*
        * rankingKey -> [username_1(score_1), username_2(score_2), ...]
        * score_1 > score_2 => username_1 đứng trước username_2 trong rankingKey.
        * */
        redisTemplate.opsForZSet().incrementScore(rankingKey, event.getUsername(), 10);

        /// 3. Update milestone.
        Double newScore = redisTemplate.opsForZSet().incrementScore(milestoneKey, event.getUsername(), 1);

        if (Objects.nonNull(newScore) && isMilestone(newScore.intValue())) {
            log.info("{} reached milestone: STREAK_{} in event: {}", event.getUsername(), newScore.intValue(), event.getEventId());
            publishMilestone(event.getEventId(), event.getUsername(), newScore.intValue());
        }
    }

    private void publishMilestone(String eventId, String username, int i) {
        StreamObserver<MilestoneResponse> observer = observers.get(eventId);
        if (Objects.nonNull(observer)) {
            MilestoneResponse response = MilestoneResponse.newBuilder()
                    .setEventId(eventId)
                    .setUsername(username)
                    .setMilestoneCode("STREAK_" + i)
                    .build();
            observer.onNext(response);
        }
    }

    private boolean isMilestone(int i) {
        return (i == 3 || i == 5 || i == 8);
    }

    @Override
    public void subscriber(String eventId, StreamObserver<MilestoneResponse> responseObserver) {
        observers.put(eventId, responseObserver);
    }

    @Override
    public void unsubscribe(String eventId) {
        log.info("Unsubscribing milestone update from eventId: {}", eventId);
        StreamObserver<MilestoneResponse> observer = observers.remove(eventId);
        if (Objects.nonNull(observer)) {
            observer.onCompleted();
            observers.remove(eventId);
        }
    }

    @Override
    @Async
    public void cleanupMilestoneData(String eventId) {
        try {
            this.unsubscribe(eventId);
            String milestoneKey = "event:" + eventId + ":milestone";
            redisTemplate.delete(milestoneKey);
            log.info("Cleaned up milestone data for eventId: {}", eventId);
        } catch (Exception e) {
            log.error("Error during cleanup milestone data for eventId: {}", eventId, e);
        }
    }
}