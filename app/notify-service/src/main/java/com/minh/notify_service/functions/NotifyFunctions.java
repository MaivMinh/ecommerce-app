package com.minh.notify_service.functions;

import com.minh.common.functions.input.NotifyOrderConfirmedEvent;
import com.minh.notify_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class NotifyFunctions {
    private final NotificationService notificationService;

    @Bean
    public Consumer<NotifyOrderConfirmedEvent> handleNotifyOrderConfirmed() {
        return notificationService::handleNotifyOrderConfirmed;
    }
}
