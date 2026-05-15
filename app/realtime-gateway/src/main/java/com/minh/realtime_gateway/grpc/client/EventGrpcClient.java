package com.minh.realtime_gateway.grpc.client;

import com.minh.common.enums.GameEventType;
import com.minh.realtime_gateway.payload.response.MilestoneUpdateResponse;
import com.minh.realtime_gateway.session.SessionRegistry;
import event_service.EventServiceGrpc;
import event_service.MilestoneRequest;
import event_service.MilestoneResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventGrpcClient {
    private final SessionRegistry sessionRegistry;

    @GrpcClient("event-service")
    private EventServiceGrpc.EventServiceStub eventServiceStub;

    public void updateMilestone(MilestoneRequest request) {
        eventServiceStub.streamMilestone(request, new StreamObserver<>() {
            @Override
            public void onNext(MilestoneResponse value) {
                log.info("Received milestone update response: {}", value);
                notifyMilestone(value);
            }

            @Override
            public void onError(Throwable t) {
                log.error("Milestone update failed", t);
            }

            @Override
            public void onCompleted() {
                log.info("Milestone update stream completed");
            }
        });

    }

    private void notifyMilestone(MilestoneResponse value) {
        MilestoneUpdateResponse response = MilestoneUpdateResponse.builder()
                .type(GameEventType.MILESTONE_UPDATE.name())
                .eventId(value.getEventId())
                .milestoneCode(value.getMilestoneCode())
                .username(value.getUsername())
                .description(value.getDescription())
                .build();

        sessionRegistry.broadcast(response);
    }
}
