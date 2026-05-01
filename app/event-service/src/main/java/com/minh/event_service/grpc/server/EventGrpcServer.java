package com.minh.event_service.grpc.server;

import com.minh.event_service.service.GameLogicHandler;
import com.minh.event_service.service.VoucherService;
import event_service.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class EventGrpcServer extends EventServiceGrpc.EventServiceImplBase {
    private final GameLogicHandler gameLogicHandler;
    private final VoucherService voucherService;

    @Override
    public void streamMilestone(MilestoneRequest request, StreamObserver<MilestoneResponse> responseObserver) {
        gameLogicHandler.subscriber(request.getEventId(), responseObserver);
    }

    @Override
    public void updateVoucher(UpdateVoucherRequest request, StreamObserver<UpdateVoucherResponse> responseObserver) {
        log.info("Nhận yêu cầu update voucher");
        UpdateVoucherResponse response = voucherService.updateVoucherGrpc(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
