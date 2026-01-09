package com.minh.realtime_gateway.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UiEvent {
    private String type;          // Loại event UI
    private String questionId;     // Câu hỏi hiện tại
    private Object payload;        // Nội dung cụ thể theo type
    private long serverTime;       // Đồng bộ thời gian
}