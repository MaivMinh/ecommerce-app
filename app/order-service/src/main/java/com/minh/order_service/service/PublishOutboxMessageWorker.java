package com.minh.order_service.service;

import com.minh.order_service.entity.OutboxMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublishOutboxMessageWorker {

    private final OutboxMessageService outboxMessageService;

    @Scheduled(fixedDelay = 1000)
    public void publishOutboxMessages() {
        List<OutboxMessage> messages = outboxMessageService.findAllUnprocessedMessages();
        for (OutboxMessage message : messages) {
            log.info("Publishing message with ID: {} and topic: {}", message.getId(), message.getTopic());
            try {
                outboxMessageService.publishMessage(message);
            } catch (Exception e) {
                log.error("Failed to publish message with ID: {}. Error: {}", message.getId(), e.getMessage());;
            }
        }
    }
}
