package com.minh.notify_service.grpc.client;

import com.minh.common.constants.ResponseMessages;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.TimeLimiter;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import support_service.GetUserInfoRequest;
import support_service.GetUserInfoResponse;
import support_service.SupportServiceGrpc;

import java.util.concurrent.CompletableFuture;

@Service
public class SupportGrpcClient {
    @GrpcClient("support-service")
    private SupportServiceGrpc.SupportServiceBlockingStub supportServiceBlockingStub;

    private GetUserInfoResponse getUserInfo(GetUserInfoRequest request, Throwable throwable) {
        return GetUserInfoResponse.newBuilder().setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()).setMessage(ResponseMessages.INTERNAL_SERVER_ERROR).build();
    }

    @CircuitBreaker(
            name = "support-service",
            fallbackMethod = "getUserInfo"
    )
    public GetUserInfoResponse getUserInfo(GetUserInfoRequest request) throws Exception {
        TimeLimiter timeLimiter = TimeLimiter.ofDefaults("support-service");
        return timeLimiter.executeFutureSupplier(() -> CompletableFuture.supplyAsync(() -> this.supportServiceBlockingStub.getUserInfo(request)));
    }
}
