package com.minh.event_service.service;

import com.minh.common.response.ResponseData;
import com.minh.event_service.payload.request.CreateAttendEventRequest;
import com.minh.event_service.payload.request.SearchAttendsEventRequest;
import com.minh.event_service.payload.request.UpdateAttendEventRequest;

public interface AttendService {
    void createAttendEvent(CreateAttendEventRequest request);

    ResponseData searchAttendsEvent(SearchAttendsEventRequest request);

    void updateAttendEvent(UpdateAttendEventRequest request);
}
