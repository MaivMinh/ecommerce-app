package com.minh.event_service.service;

import com.minh.common.response.ResponseData;

public interface EventService {
    /** Đăng kí tham gia sự kiện cho người dùng hiện tại dựa trên chiến dịch. */
    ResponseData registerEventAttendance(String campaignId);
}
