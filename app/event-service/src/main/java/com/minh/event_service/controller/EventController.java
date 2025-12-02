package com.minh.event_service.controller;

import com.minh.common.response.ResponseData;
import com.minh.event_service.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/events")
@RequiredArgsConstructor
@Validated
public class EventController {
    private final EventService eventService;

    @PostMapping(value = "/{campaignId}/register")
    public ResponseEntity<ResponseData> registerEventAttendance(@PathVariable(name = "campaignId") String campaignId) {
        ResponseData response = eventService.registerEventAttendance(campaignId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}