package com.minh.order_service.grpc.client;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.TimeLimiter;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import product_service.FindProductVariantByListProductVariantIdRequest;
import product_service.FindProductVariantByListProductVariantIdResponse;
import product_service.ProductServiceGrpc;

@Service
public class ProductGrpcClient {
    @GrpcClient("product-service")
    private ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub;
    @Autowired
    private TimeLimiter productServiceTimeLimiter;

    private FindProductVariantByListProductVariantIdResponse findProductVariantByListId(FindProductVariantByListProductVariantIdRequest req, Throwable throwable) {
        String message = throwable instanceof CallNotPermittedException
                ? "Không có kết nối tới product service. Vui lòng thử lại sau."
                : "Có lỗi xảy ra khi gọi tới product service: "
                + throwable.getMessage();
        return FindProductVariantByListProductVariantIdResponse.newBuilder()
                .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setMessage(message)
                .build();
    }

    @CircuitBreaker(name = "product-service", fallbackMethod = "findProductVariantByListId")
    public FindProductVariantByListProductVariantIdResponse findProductVariantByListId(FindProductVariantByListProductVariantIdRequest request) throws Exception {
        return productServiceTimeLimiter.executeFutureSupplier(
                () -> java.util.concurrent.CompletableFuture.supplyAsync(
                        () -> this.productServiceBlockingStub.findProductVariantByListId(request)
                )
        );

    }
}
