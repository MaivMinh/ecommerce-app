package com.minh.order_service.config;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import payment_service.PaymentServiceGrpc;
import product_service.ProductServiceGrpc;
import support_service.SupportServiceGrpc;

@Configuration
@RequiredArgsConstructor
public class GrpcClientConfig {
    private final Environment env;
    private final TimeLimiterRegistry timeLimiterRegistry;

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
    public ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub() {
        String address = env.getProperty("PRODUCT_GRPC_SERVER", "localhost:9091");  /// Nếu sử dụng các version grpc cũ hơn thì loại bỏ static phía trước.
        return ProductServiceGrpc.newBlockingStub(ManagedChannelBuilder.forTarget(address).usePlaintext().keepAliveWithoutCalls(true).build());
    }

    @Bean
    public SupportServiceGrpc.SupportServiceBlockingStub supportServiceBlockingStub() {
        String address = env.getProperty("SUPPORT_GRPC_SERVER", "localhost:9096");
        return SupportServiceGrpc.newBlockingStub(io.grpc.ManagedChannelBuilder.forTarget(address).keepAliveWithoutCalls(true).usePlaintext().build());
    }

    @Bean
    public PaymentServiceGrpc.PaymentServiceBlockingStub paymentServiceBlockingStub() {
        String address = env.getProperty("PAYMENT_GRPC_SERVER", "localhost:9095");
        return PaymentServiceGrpc.newBlockingStub(io.grpc.ManagedChannelBuilder.forTarget(address).keepAliveWithoutCalls(true).usePlaintext().build());
    }
}
