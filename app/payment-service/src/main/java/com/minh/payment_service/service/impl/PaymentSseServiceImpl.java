package com.minh.payment_service.service.impl;

import com.minh.payment_service.config.PayPalProperties;
import com.minh.payment_service.entity.Payment;
import com.minh.payment_service.enums.PaymentStatus;
import com.minh.payment_service.payload.response.PayPalRedirectPayload;
import com.minh.payment_service.repository.PaymentRepository;
import com.minh.payment_service.service.PaymentSseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentSseServiceImpl implements PaymentSseService {
    private static final String REDIRECT_EVENT = "paypal-redirect";
    private final ConcurrentHashMap<String, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();
    private final PaymentRepository paymentRepository;
    private final PayPalProperties payPalProperties;

    @Override
    public SseEmitter subscribe(String username) {
        SseEmitter emitter = new SseEmitter(payPalProperties.getSseTimeoutMs());
        emitters.computeIfAbsent(username, key -> ConcurrentHashMap.newKeySet()).add(emitter);
        emitter.onCompletion(() -> removeEmitter(username, emitter));
        emitter.onTimeout(() -> removeEmitter(username, emitter));
        emitter.onError(error -> removeEmitter(username, emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("connected"));
            replayPendingRedirects(username, emitter);
        } catch (IOException exception) {
            removeEmitter(username, emitter);
            throw new RuntimeException("Không thể khởi tạo kết nối SSE.", exception);
        }
        return emitter;
    }

    @Override
    public void publishRedirect(Payment payment) {
        if (payment == null || payment.getStatus() != PaymentStatus.PENDING || payment.getApprovalUrl() == null) {
            return;
        }

        PayPalRedirectPayload payload = PayPalRedirectPayload.builder()
                .orderId(payment.getOrderId())
                .paymentId(payment.getId())
                .paypalOrderId(payment.getProviderOrderId())
                .redirectUrl(payment.getApprovalUrl())
                .status(payment.getStatus().name())
                .build();

        Set<SseEmitter> userEmitters = emitters.getOrDefault(payment.getUsername(), Set.of());
        if (userEmitters.isEmpty()) {
            return;
        }

        userEmitters.forEach(emitter -> sendRedirectEvent(payment.getUsername(), emitter, payload));
    }

    private void replayPendingRedirects(String username, SseEmitter emitter) throws IOException {
        List<Payment> payments = paymentRepository.findAllByUsernameAndStatusAndApprovalUrlIsNotNull(username, PaymentStatus.PENDING);
        for (Payment payment : payments) {
            PayPalRedirectPayload payload = PayPalRedirectPayload.builder()
                    .orderId(payment.getOrderId())
                    .paymentId(payment.getId())
                    .paypalOrderId(payment.getProviderOrderId())
                    .redirectUrl(payment.getApprovalUrl())
                    .status(payment.getStatus().name())
                    .build();
            emitter.send(SseEmitter.event()
                    .name(REDIRECT_EVENT)
                    .data(payload));
        }
    }

    private void sendRedirectEvent(String username, SseEmitter emitter, PayPalRedirectPayload payload) {
        try {
            emitter.send(SseEmitter.event()
                    .name(REDIRECT_EVENT)
                    .data(payload));
        } catch (IOException exception) {
            log.warn("SSE push failed for user {}", username, exception);
            removeEmitter(username, emitter);
        }
    }

    private void removeEmitter(String username, SseEmitter emitter) {
        Set<SseEmitter> userEmitters = emitters.get(username);
        if (userEmitters == null) {
            return;
        }
        userEmitters.remove(emitter);
        if (userEmitters.isEmpty()) {
            emitters.remove(username);
        }
    }
}
