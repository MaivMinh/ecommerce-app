package com.minh.event_service.grpc.client;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.TimeLimiter;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import support_service.SupportServiceGrpc;
import support_service.VerifyUserRequest;
import support_service.VerifyUserResponse;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class SupportGrpcClient {
    @GrpcClient("support-service")
    private SupportServiceGrpc.SupportServiceBlockingStub supportServiceBlockingStub;

    private final TimeLimiter timeLimiter = TimeLimiter.ofDefaults("support-service");


    private VerifyUserResponse verifyUser(VerifyUserRequest req, Throwable throwable) {
        String message = throwable instanceof CallNotPermittedException
                ? "Không có kết nối tới support service. Vui lòng thử lại sau."
                : "Có lỗi xảy ra khi gọi tới support service: "
                + throwable.getMessage();
        return VerifyUserResponse.newBuilder()
                .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setMessage(message)
                .build();
    }

    @CircuitBreaker(
            name = "support-service",
            fallbackMethod = "verifyUser"
    )
    public VerifyUserResponse verifyUser(VerifyUserRequest req) throws Exception {
        return this.timeLimiter.executeFutureSupplier(
                () -> CompletableFuture.supplyAsync(
                        () -> this.supportServiceBlockingStub.verifyUser(req)
                )
        );
    }
}
