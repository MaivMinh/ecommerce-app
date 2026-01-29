package com.minh.order_service.grpc.client;

import game_service.GetShippingAddressRequest;
import game_service.GetShippingAddressResponse;
import game_service.SupportServiceGrpc;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.TimeLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class SupportGrpcClient {
    @Autowired
    private SupportServiceGrpc.SupportServiceBlockingStub supportServiceBlockingStub;   /// Phải thực hiện ta Bean trong gRPC Config.
    @Autowired
    private TimeLimiter supportServiceTimeLimiter;

    private GetShippingAddressResponse getShippingAddress(GetShippingAddressRequest request, Throwable throwable) {
        String message = throwable instanceof CallNotPermittedException ? "Không có kết nối tới support service. Vui lòng thử lại sau." : "Có lỗi xảy ra khi gọi tới support service: " + throwable.getMessage();
        return GetShippingAddressResponse.newBuilder().setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()).setMessage(message).build();
    }

    @CircuitBreaker(
            name = "support-service",
            fallbackMethod = "getShippingAddress"
    )
    public GetShippingAddressResponse getShippingAddress(GetShippingAddressRequest request) throws Exception {
        return supportServiceTimeLimiter.executeFutureSupplier(
                () -> CompletableFuture.supplyAsync(
                        () -> this.supportServiceBlockingStub.getShippingAddress(request)
                )
        );
    }
}
