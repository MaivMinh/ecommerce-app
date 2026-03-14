package com.minh.order_service.service;

import com.minh.common.commands.SagaCommand;
import com.minh.common.events.SagaEvent;
import com.minh.order_service.entity.OutboxMessage;

import java.util.List;

public interface OutboxMessageService {
    void store(SagaCommand command, String topic, String className);
    void store(SagaEvent event, String topic, String className);

    List<OutboxMessage> findAllUnprocessedMessages();

    void publishMessage(OutboxMessage message);
}
