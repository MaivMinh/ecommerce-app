package com.minh.payment_service.grpc.server;

import com.minh.payment_service.service.PaymentService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import payment_service.GetPaymentStatusRequest;
import payment_service.GetPaymentStatusResponse;
import payment_service.PaymentServiceGrpc;

@GrpcService
@RequiredArgsConstructor
public class PaymentGrpcServer extends PaymentServiceGrpc.PaymentServiceImplBase {
    private final PaymentService paymentService;

    @Override
    public void getPaymentStatus(GetPaymentStatusRequest request, StreamObserver<GetPaymentStatusResponse> responseObserver) {
        GetPaymentStatusResponse response = paymentService.getPaymentStatus(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
