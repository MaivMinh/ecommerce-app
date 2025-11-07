package com.minh.notify_service.service;

import com.minh.common.response.ResponseData;
import com.minh.notify_service.payload.request.SearchSendLogsRequest;

public interface SendLogService {
    ResponseData getSendLogById(String logId);

    ResponseData searchSendLogs(SearchSendLogsRequest request);
}
