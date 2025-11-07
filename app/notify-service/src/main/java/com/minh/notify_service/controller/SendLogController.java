package com.minh.notify_service.controller;

import com.minh.common.response.ResponseData;
import com.minh.notify_service.payload.request.SearchSendLogsRequest;
import com.minh.notify_service.service.SendLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping(value = "/api/send-logs")
@RequiredArgsConstructor
public class SendLogController {
    private final SendLogService sendLogService;

    @GetMapping("{logId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> getSendLogById(@PathVariable(name = "logId") String logId) {
        ResponseData response = sendLogService.getSendLogById(logId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(value = "/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> searchSendLogs(@RequestBody SearchSendLogsRequest request) {
        ResponseData response = sendLogService.searchSendLogs(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
