package com.minh.payment_service.factory;

import com.minh.payment_service.enums.PaymentProvider;
import com.minh.payment_service.service.PaymentStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class PaymentStrategyFactory {
    private final Map<PaymentProvider, PaymentStrategy> paymentStrategies;

    public PaymentStrategyFactory(List<PaymentStrategy> strategies) {
        paymentStrategies = strategies.stream()
                .collect(java.util.stream.Collectors.toMap(
                        PaymentStrategy::getProvider,
                        strategy -> strategy
                ));
    }

    public PaymentStrategy getPaymentStrategy(PaymentProvider provider) {
        return Optional.ofNullable(paymentStrategies.get(provider))
                .orElseThrow(() -> new IllegalArgumentException("Nhà cung cấp thanh toán khôgn hợp lệ: " + provider));
    }
}
