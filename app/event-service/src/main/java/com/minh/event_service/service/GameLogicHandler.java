package com.minh.event_service.service;

import com.minh.event_service.DTO.WsMessage;

public interface GameLogicHandler {
    /**
     * Hàm xử lý toàn bộ logic của game khi người chơi thực hiện trả lời câu hỏi.
     * @param event: Dữ liệu trả lời câu hỏi từ người chơi.
     */
    void handlePlayAnswer(WsMessage event);
}
