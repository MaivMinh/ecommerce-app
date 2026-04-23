package com.minh.order_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.common.commands.*;
import com.minh.common.enums.MessageType;
import com.minh.common.events.OrderCompletionFailedEvent;
import com.minh.common.events.SagaEvent;
import com.minh.common.utils.AppUtils;
import com.minh.order_service.entity.OutboxMessage;
import com.minh.order_service.repository.OutboxMessageRepository;
import com.minh.order_service.service.OutboxMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxMessageServiceImpl implements OutboxMessageService {
    private final OutboxMessageRepository repository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public void store(SagaCommand command, String topic, String className) {
        try {
            OutboxMessage message = new OutboxMessage();
            message.setId(AppUtils.generateUUIDv7());
            message.setProcessed(Boolean.FALSE);
            message.setType(MessageType.COMMAND);
            message.setTopic(topic);
            message.setPayload(objectMapper.writeValueAsString(command));
            message.setClassName(className);
            repository.save(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize command payload", e);
        }
    }

    @Override
    @Transactional
    public void store(SagaEvent event, String topic, String className) {
        try {
            OutboxMessage message = new OutboxMessage();
            message.setId(AppUtils.generateUUIDv7());
            message.setProcessed(Boolean.FALSE);
            message.setType(MessageType.COMMAND);
            message.setTopic(topic);
            message.setPayload(objectMapper.writeValueAsString(event));
            message.setClassName(className);
            repository.save(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event payload", e);
        }
    }

    @Override
    public List<OutboxMessage> findAllUnprocessedMessages() {
        return repository.findAllByProcessed();
    }

    @Override
    @Transactional
    public void publishMessage(OutboxMessage message) {
        if (message.getType().equals(MessageType.COMMAND)) {
            try {
                switch (message.getClassName()) {
                    case "com.minh.common.commands.ReserveProductCommand":
                        ReserveProductCommand command = objectMapper.readValue(message.getPayload(), ReserveProductCommand.class);
                        command.setMessageId(message.getId());
                        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(message.getTopic(), command.getSagaId(), command);
                        this.handleSendResult(future, message);
                        break;
                    case "com.minh.common.commands.ProcessPaymentCommand":
                        ProcessPaymentCommand ppcCommand = objectMapper.readValue(message.getPayload(), ProcessPaymentCommand.class);
                        ppcCommand.setMessageId(message.getId());
                        this.handleSendResult(
                                kafkaTemplate.send(message.getTopic(), ppcCommand.getSagaId(), ppcCommand),
                                message
                        );
                        break;
                    case "com.minh.common.commands.RefundProcessedPaymentCommand":
                        RefundProcessedPaymentCommand rppCommand = objectMapper.readValue(message.getPayload(), RefundProcessedPaymentCommand.class);
                        rppCommand.setMessageId(message.getId());
                        this.handleSendResult(
                                kafkaTemplate.send(message.getTopic(), rppCommand.getSagaId(), rppCommand),
                                message
                        );
                        break;
                    case "com.minh.common.commands.ReleaseProductCommand":
                        ReleaseProductCommand rpcCommand = objectMapper.readValue(message.getPayload(), ReleaseProductCommand.class);
                        rpcCommand.setMessageId(message.getId());
                        this.handleSendResult(
                                kafkaTemplate.send(message.getTopic(), rpcCommand.getSagaId(), rpcCommand),
                                message
                        );
                        break;
                    default:
                        throw new RuntimeException("Unknown command class: " + message.getClassName());
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize command payload", e);
            }
        } else if (message.getType().equals(MessageType.EVENT)) {
            try {
                switch (message.getClassName()) {
                    case "com.minh.common.events.OrderCompletionFailedEvent":
                        OrderCompletionFailedEvent orderCompletionFailedEvent = objectMapper.readValue(message.getPayload(), OrderCompletionFailedEvent.class);
                        this.handleSendResult(
                                kafkaTemplate.send(message.getTopic(), orderCompletionFailedEvent.getSagaId(), orderCompletionFailedEvent),
                                message
                        );
                        break;
                    default:
                        throw new RuntimeException("Unknown event class: " + message.getClassName());
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize event payload", e);
            }
        }
    }

    private void handleSendResult(CompletableFuture<SendResult<String, Object>> future, OutboxMessage message) {
        future.whenComplete((result, ex) -> {
            if (Objects.isNull(ex)) {
                /// ex == null -> Success.
                message.setProcessed(Boolean.TRUE);
                message.setProcessedAt(Instant.now());
                repository.save(message);
                ///  nếu dùng Redis lock, thì chỗ này sẽ release lock đó. Vì lúc này status mới của message đã được cập nhật là xử lý dưới DB rồi.
            } else {
                log.error("Failed to publish message with ID: {}. Error: {}", message.getId(), ex.getMessage());
            }
        });
    }
}
