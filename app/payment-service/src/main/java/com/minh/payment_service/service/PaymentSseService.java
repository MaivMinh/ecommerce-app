package com.minh.payment_service.service;

import com.minh.payment_service.entity.Payment;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface PaymentSseService {
    SseEmitter subscribe(String username);

    void publishRedirect(Payment payment);
}
