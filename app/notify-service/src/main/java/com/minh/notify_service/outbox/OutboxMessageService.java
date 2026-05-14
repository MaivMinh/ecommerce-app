package com.minh.notify_service.outbox;

import com.minh.common.functions.input.NotifyEvent;
import com.minh.notify_service.entity.OutboxMessage;

import java.util.List;

public interface OutboxMessageService {

    void store(String topic, NotifyEvent event, String orderId, String className);

    void publishMessage(OutboxMessage message);

    List<OutboxMessage> findTopNUnprocessedMessage(int i);

    boolean isMessageProcessed(String messageId);
}
