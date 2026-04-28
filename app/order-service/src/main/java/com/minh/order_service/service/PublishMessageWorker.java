package com.minh.order_service.service;

import com.minh.order_service.entity.OutboxMessage;
import com.minh.order_service.outbox.OutboxMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class PublishMessageWorker {
    private final ExecutorService service;
    private final OutboxMessageService outboxMessageService;

    public PublishMessageWorker(OutboxMessageService outboxMessageService) {
        this.service = Executors.newFixedThreadPool(10);
        this.outboxMessageService = outboxMessageService;
    }

    @Scheduled(fixedDelay = 500)
    public void publishMessage() {
        List<OutboxMessage> messages = outboxMessageService.findTopNUnprocessedMessage(500);

        for (OutboxMessage message : messages) {
            service.submit(() -> {
                try {
                    log.info("Publishing message with id: {}", message.getId());
                    outboxMessageService.publishMessage(message);
                } catch (Exception e) {
                    log.error("Có lỗi xảy ra khi publish message với id: {}, error: {}", message.getId(), e.getMessage());
                }
            });
        }
    }
}
