package com.minh.order_service.grpc.client;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.timelimiter.TimeLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import product_service.FindProductVariantByListProductVariantIdRequest;
import product_service.FindProductVariantByListProductVariantIdResponse;

@Service
public class ProductGrpcClient {
    @Autowired
    private product_service.ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub;
    @Autowired
    private TimeLimiter productServiceTimeLimiter;

    private FindProductVariantByListProductVariantIdResponse findProductVariantByListId(FindProductVariantByListProductVariantIdRequest req, Throwable throwable) {
        String message = throwable instanceof CallNotPermittedException
                ? "Không có kết nối tới support service. Vui lòng thử lại sau."
                : "Có lỗi xảy ra khi gọi tới product service: "
                + throwable.getMessage();
        return FindProductVariantByListProductVariantIdResponse.newBuilder()
                .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setMessage(message)
                .build();
    }
    public FindProductVariantByListProductVariantIdResponse findProductVariantByListId(FindProductVariantByListProductVariantIdRequest request) throws Exception {
        return productServiceTimeLimiter.executeFutureSupplier(
                () -> java.util.concurrent.CompletableFuture.supplyAsync(
                        () -> this.productServiceBlockingStub.findProductVariantByListId(request)
                )
        );

    }
}
