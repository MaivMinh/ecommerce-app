package com.minh.order_service.config;

import event_service.EventServiceGrpc;
import game_service.SupportServiceGrpc;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import payment_service.PaymentServiceGrpc;
import product_service.ProductServiceGrpc;

@Configuration
@RequiredArgsConstructor
public class GrpcClientConfig {
    private final TimeLimiterRegistry timeLimiterRegistry;

    @Value("${app.grpc.server.product-service:localhost:9091}")
    private String productServiceAddress;
    @Value("${app.grpc.server.support-service:localhost:9096}")
    private String supportServiceAddress;
    @Value("${app.grpc.server.payment-service:localhost:9095}")
    private String paymentServiceAddress;
    @Value("${app.grpc.server.event-service:localhost:9097}")
    private String eventServiceAddress;


    @Bean
    public TimeLimiter productServiceTimeLimiter() {
        return timeLimiterRegistry.timeLimiter("product-service");
    }

    @Bean
    public TimeLimiter supportServiceTimeLimiter() {
        return timeLimiterRegistry.timeLimiter("support-service");
    }

    @Bean
    public TimeLimiter paymentServiceTimeLimiter() {
        return timeLimiterRegistry.timeLimiter("payment-service");
    }

    @Bean
    public TimeLimiter eventServiceTimeLimiter() {
        return timeLimiterRegistry.timeLimiter("event-service");
    }

    @Bean
    public ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub() {
        return ProductServiceGrpc.newBlockingStub(ManagedChannelBuilder.forTarget(productServiceAddress).usePlaintext().keepAliveWithoutCalls(true).build());
    }

    @Bean
    public SupportServiceGrpc.SupportServiceBlockingStub supportServiceBlockingStub() {
        return SupportServiceGrpc.newBlockingStub(io.grpc.ManagedChannelBuilder.forTarget(supportServiceAddress).keepAliveWithoutCalls(true).usePlaintext().build());
    }

    @Bean
    public PaymentServiceGrpc.PaymentServiceBlockingStub paymentServiceBlockingStub() {
        return PaymentServiceGrpc.newBlockingStub(io.grpc.ManagedChannelBuilder.forTarget(paymentServiceAddress).keepAliveWithoutCalls(true).usePlaintext().build());
    }

    @Bean
    public EventServiceGrpc.EventServiceBlockingStub eventServiceBlockingStub() {
        return EventServiceGrpc.newBlockingStub(ManagedChannelBuilder.forTarget(eventServiceAddress).keepAliveWithoutCalls(true).usePlaintext().build());
    }
}
