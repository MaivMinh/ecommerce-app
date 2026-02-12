package com.minh.realtime_gateway.service;

import com.minh.realtime_gateway.DTOs.RealtimeEvent;
import org.springframework.scheduling.annotation.Async;

public interface MilestoneService {

    /**
     * Hàm thực hiện cập nhật lại milestone nếu người chơi đạt được.
     * @param event: Sự kiện hiện tại mà người chơi tham gia.
     */
    @Async
    void updateMilestone(RealtimeEvent event);
}
