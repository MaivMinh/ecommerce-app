package com.minh.order_service.grpc.client;

import io.github.resilience4j.timelimiter.TimeLimiter;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import payment_service.GetPaymentStatusRequest;
import payment_service.GetPaymentStatusResponse;
import payment_service.PaymentServiceGrpc;

@Service
public class PaymentGrpcClient {
    @Autowired
    private PaymentServiceGrpc.PaymentServiceBlockingStub paymentServiceBlockingStub;
    @Autowired
    private TimeLimiter paymentServiceTimeLimiter;

    private GetPaymentStatusResponse getPaymentStatus(GetPaymentStatusRequest paymentStatusRequest, Throwable throwable) {
        String message = "Có lỗi xảy ra khi gọi tới payment service: " + throwable.getMessage();
        return GetPaymentStatusResponse.newBuilder().setStatus(500).setMessage(message).build();
    }

    public GetPaymentStatusResponse getPaymentStatus(GetPaymentStatusRequest paymentStatusRequest) throws Exception {
        return paymentServiceTimeLimiter.executeFutureSupplier(
                () -> java.util.concurrent.CompletableFuture.supplyAsync(
                        () -> this.paymentServiceBlockingStub.getPaymentStatus(paymentStatusRequest)
                )
        );
    }
}