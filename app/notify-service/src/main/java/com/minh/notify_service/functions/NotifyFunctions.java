package com.minh.notify_service.functions;

import com.minh.common.functions.input.NotifyOrderCancelledEvent;
import com.minh.common.functions.input.NotifyOrderCompletedEvent;
import com.minh.common.kafka.KafkaTopics;
import com.minh.notify_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NotifyFunctions {
    private final NotificationService service;

    @KafkaListener(
            topics = KafkaTopics.ORDER_COMPLETED,
            groupId = "notify-service"
    )
    @Transactional
    public void handleNotifyOrderCompleted(NotifyOrderCompletedEvent event) {
        service.handleNotifyOrderConfirmed(event);
    }

    @KafkaListener(
            topics = KafkaTopics.NOTIFY_ORDER_CANCELLED,
            groupId = "notify-service"
    )
    @Transactional
    public void handleNotifyOrderCancelled(NotifyOrderCancelledEvent event) {
        service.handleNotifyOrderCancelled(event);
    }
}
