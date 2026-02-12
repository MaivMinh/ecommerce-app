package com.minh.event_service.grpc.server;

import com.minh.event_service.service.GameLogicHandler;
import event_service.EventServiceGrpc;
import event_service.MilestoneRequest;
import event_service.MilestoneResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class EventGrpcServer extends EventServiceGrpc.EventServiceImplBase {
    private final GameLogicHandler gameLogicHandler;

    @Override
    public void streamMilestone(MilestoneRequest request, StreamObserver<MilestoneResponse> responseObserver) {
        gameLogicHandler.subscriber(request.getEventId(), responseObserver);
    }
}
