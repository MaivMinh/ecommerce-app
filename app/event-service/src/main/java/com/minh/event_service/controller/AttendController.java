package com.minh.event_service.controller;

import com.minh.common.constants.ResponseMessages;
import com.minh.common.response.ResponseData;
import com.minh.event_service.payload.request.SearchAttendsEventRequest;
import com.minh.event_service.payload.request.CreateAttendEventRequest;
import com.minh.event_service.payload.request.UpdateAttendEventRequest;
import com.minh.event_service.service.AttendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(value = "/api/attends")
public class AttendController {
    private final AttendService attendService;

    @PostMapping(value = "")
    public ResponseEntity<ResponseData> createAttendEvent(@RequestBody @Valid CreateAttendEventRequest request) {
        attendService.createAttendEvent(request);

        return ResponseEntity.ok(ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(null)
                .build()
        );
    }

    @PostMapping(value = "/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> searchAttendsEvent(@RequestBody SearchAttendsEventRequest request) {
        ResponseData response = attendService.searchAttendsEvent(request);

        return ResponseEntity.status(response.getStatus())
                .body(response);
    }

    @PutMapping(value = "")
    public ResponseEntity<ResponseData> updateAttendEvent(@RequestBody @Valid UpdateAttendEventRequest request) {
        attendService.updateAttendEvent(request);

        return ResponseEntity.ok(ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(null)
                .build()
        );
    }
}
