package com.minh.realtime_gateway.DTOs;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameEvent {
    private String questionId;
    private String type;          // Loại event UI
    private Object payload;        // Nội dung cụ thể theo type
    private long serverTime;       // Đồng bộ thời gian
}