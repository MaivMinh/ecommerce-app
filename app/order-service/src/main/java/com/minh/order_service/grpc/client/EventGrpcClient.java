package com.minh.order_service.grpc.client;

import event_service.EventServiceGrpc;
import event_service.UpdateVoucherRequest;
import event_service.UpdateVoucherResponse;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.TimeLimiter;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EventGrpcClient {
    @GrpcClient("event-service")
    private EventServiceGrpc.EventServiceBlockingStub eventServiceBlockingStub;

    @Autowired
    private TimeLimiter eventServiceTimeLimiter;

    private UpdateVoucherResponse updateVoucher(UpdateVoucherRequest request, Throwable throwable) {
        String message = throwable instanceof CallNotPermittedException
                ? "Không có kết nối tới Event Service. Vui lòng thử lại sau."
                : "Có lỗi xảy ra khi gọi tới event service: "
                  + throwable.getMessage();

        return UpdateVoucherResponse.newBuilder()
                .setStatus(500)
                .setMessages(message)
                .build();
    }

    @CircuitBreaker(name = "event-service", fallbackMethod = "updateVoucher")
    public UpdateVoucherResponse updateVoucher(UpdateVoucherRequest request) {
        try {
            return eventServiceTimeLimiter.executeFutureSupplier(
                    () -> CompletableFuture.supplyAsync(() -> this.eventServiceBlockingStub.updateVoucher(request))
            );
        } catch (Exception e) {
            return UpdateVoucherResponse.newBuilder()
                    .setStatus(500)
                    .setMessages("Có lỗi xảy ra khi thực hiện cập nhật Voucher")
                    .build();
        }
    }
}
