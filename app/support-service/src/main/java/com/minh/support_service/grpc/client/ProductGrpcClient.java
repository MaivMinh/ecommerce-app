package com.minh.support_service.grpc.client;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.TimeLimiter;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import product_service.FindProductVariantsByIdsRequest;
import product_service.FindProductVariantsByIdsResponse;
import product_service.ProductServiceGrpc;

import java.util.concurrent.CompletableFuture;

@Service
public class ProductGrpcClient {
    @GrpcClient("product-service")
    private ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub;

    private FindProductVariantsByIdsResponse findProductVariantsByIds(FindProductVariantsByIdsRequest request, Throwable throwable) {
        String message = throwable instanceof CallNotPermittedException ? "Circuit breaker đang ở trạng thái Open. Vui lòng thử lại sau." : "Có lỗi xảy ra khi gọi tới Product service: " + throwable.getMessage();
        return FindProductVariantsByIdsResponse.newBuilder().setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()).setMessage(message).build();
    }

    @CircuitBreaker(
            name = "product-service",
            fallbackMethod = "findProductVariantsByIds"
    )
    public FindProductVariantsByIdsResponse findProductVariantsByIds(FindProductVariantsByIdsRequest request) throws Exception {
        TimeLimiter timeLimiter = TimeLimiter.ofDefaults("product-service");
        return timeLimiter.executeFutureSupplier(() -> CompletableFuture.supplyAsync(() -> this.productServiceBlockingStub.findProductVariantsByIds(request)));
    }
}