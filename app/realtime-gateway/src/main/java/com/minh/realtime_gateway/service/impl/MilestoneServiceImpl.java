package com.minh.realtime_gateway.service.impl;

import com.minh.realtime_gateway.DTOs.RealtimeEvent;
import com.minh.realtime_gateway.grpc.client.EventGrpcClient;
import com.minh.realtime_gateway.service.MilestoneService;
import event_service.MilestoneRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MilestoneServiceImpl implements MilestoneService {
    private final EventGrpcClient eventGrpcClient;

    @Override
    @Async
    public void updateMilestone(RealtimeEvent event) {
        try {
            if (Objects.isNull(event) || !StringUtils.hasText(event.getEventId())) {
                log.error("Milestone update failed: invalid event data");
                return;
            }
            MilestoneRequest request = MilestoneRequest.newBuilder()
                    .setEventId(event.getEventId())
                    .build();

            eventGrpcClient.updateMilestone(request);
        }   catch (Exception e) {
            log.error("Error while updating milestone for event: {}", event, e);
        }
    }
}
