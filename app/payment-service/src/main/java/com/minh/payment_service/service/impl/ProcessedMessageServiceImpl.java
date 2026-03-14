package com.minh.payment_service.service.impl;

import com.minh.common.commands.SagaCommand;
import com.minh.payment_service.entity.ProcessedMessage;
import com.minh.payment_service.repository.ProcessedMessageRepository;
import com.minh.payment_service.service.ProcessedMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ProcessedMessageServiceImpl implements ProcessedMessageService {
    private final ProcessedMessageRepository repository;

    @Override
    @Transactional
    public void store(SagaCommand command) {
        ProcessedMessage message = new ProcessedMessage();
        message.setId(command.getMessageId());
        message.setProcessedAt(Instant.now());
        repository.save(message);
    }

    @Override
    public boolean isMessageProcessed(String messageId) {
        ProcessedMessage message = repository.findById(messageId).orElse(null);
        return message != null;
    }
}
