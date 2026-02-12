package com.minh.event_service.service;

import com.minh.event_service.DTO.WsMessage;
import event_service.MilestoneResponse;
import io.grpc.stub.StreamObserver;

public interface GameLogicHandler {
    /**
     * Hàm xử lý toàn bộ logic của game khi người chơi thực hiện trả lời câu hỏi.
     * @param event: Dữ liệu trả lời câu hỏi từ người chơi.
     */
    void handlePlayAnswer(WsMessage event);

    /**
     * Hàm đăng ký lắng nghe các mốc điểm của sự kiện game.
     * @param eventId: ID của sự kiện game.
     * @param responseObserver: Đối tượng dùng để gửi phản hồi về cho client.
     */
    void subscriber(String eventId, StreamObserver<MilestoneResponse> responseObserver);

    void unsubscribe(String eventId);

    void cleanupMilestoneData(String eventId);
}
