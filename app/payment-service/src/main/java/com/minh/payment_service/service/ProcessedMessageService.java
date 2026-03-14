package com.minh.payment_service.service;

import com.minh.common.commands.SagaCommand;

public interface ProcessedMessageService {
    void store(SagaCommand command);

    boolean isMessageProcessed(String messageId);
}
