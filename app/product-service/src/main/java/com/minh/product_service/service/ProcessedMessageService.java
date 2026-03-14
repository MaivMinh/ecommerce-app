package com.minh.product_service.service;

import com.minh.common.commands.SagaCommand;
import com.minh.common.events.SagaEvent;

public interface ProcessedMessageService {

    void store(SagaCommand command);
    void store(SagaEvent event);

    boolean isMessageProcessed(String messageId);
}
