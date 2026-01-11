package com.minh.event_service.enums;

public enum GameEventType {
    GAME_READY("Người chơi sẵn sàng. Trò chơi sẽ bắt đầu sau 30s nữa", 30),
    GAME_START("Trò chơi sẽ bắt đầu trong 5s nữa", 5),
    QUESTION("Hiển thị câu hỏi", 30),
    SCORING("Hệ thống đang chấm điểm. Vui lòng chờ...", 30),
    GAME_RESULT("Kết quả của trò chơi", 0),
    CLEANUP("Xóa dữ liệu trong Redis khi kết thúc trò chơi", 0);

    GameEventType(String description, Integer value) {
        this.description = description;
        this.value = value;
    }
    private final String description;
    private final Integer value;

    public String getDescription() {
        return description;
    }
    public Integer getValue() {
        return value;
    }
}
