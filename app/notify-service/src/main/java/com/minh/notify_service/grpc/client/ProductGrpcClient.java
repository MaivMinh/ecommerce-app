package com.minh.notify_service.grpc.client;

import com.minh.common.constants.ResponseMessages;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.TimeLimiter;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import product_service.FindProductInfoByProductVariantIdRequest;
import product_service.FindProductInfoByProductVariantIdResponse;

import java.util.concurrent.CompletableFuture;

@Service
public class ProductGrpcClient {
    @GrpcClient("product-service")
    private product_service.ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub;

    private FindProductInfoByProductVariantIdResponse findProductInfoByProductVariantId(FindProductInfoByProductVariantIdRequest request, Throwable throwable) {
        return FindProductInfoByProductVariantIdResponse.newBuilder().setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()).setMessage(ResponseMessages.INTERNAL_SERVER_ERROR).build();
    }

    @CircuitBreaker(
            name = "product-service",
            fallbackMethod = "findProductInfoByProductVariantId"
    )
    public FindProductInfoByProductVariantIdResponse findProductInfoByProductVariantId(FindProductInfoByProductVariantIdRequest request) throws Exception {
        TimeLimiter timeLimiter = TimeLimiter.ofDefaults("product-service");
        return timeLimiter.executeFutureSupplier(() -> CompletableFuture.supplyAsync(() -> this.productServiceBlockingStub.findProductInfoByProductVariantId(request)));
    }
}
