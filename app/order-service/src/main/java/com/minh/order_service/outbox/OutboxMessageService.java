package com.minh.order_service.outbox;

import com.minh.common.commands.SagaCommand;
import com.minh.common.events.SagaEvent;
import com.minh.order_service.entity.OutboxMessage;

import java.util.List;

public interface OutboxMessageService {

    void store(String topic, SagaEvent event, String className);

    void store(String topic, SagaCommand command, String className);

    void publishMessage(OutboxMessage message);

    List<OutboxMessage> findTopNUnprocessedMessage(int i);
}
