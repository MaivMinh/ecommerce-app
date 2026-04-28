package com.minh.order_service.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.common.commands.*;
import com.minh.common.enums.MessageType;
import com.minh.common.events.*;
import com.minh.common.utils.AppUtils;
import com.minh.order_service.entity.OutboxMessage;
import com.minh.order_service.repository.OutboxMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxMessageServiceImpl implements OutboxMessageService {

    private final ObjectMapper objectMapper;
    private final OutboxMessageRepository outboxMessageRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void store(String topic, SagaEvent event, String className) {
        try {
            OutboxMessage message = OutboxMessage.builder()
                    .id(AppUtils.generateUUIDv7())
                    .messageId(event.getMessageId())
                    .processed(Boolean.FALSE)
                    .processedAt(null)
                    .type(MessageType.EVENT)
                    .topic(topic)
                    .payload(objectMapper.writeValueAsString(event))
                    .className(className)
                    .build();

            outboxMessageRepository.save(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize event payload", e);
        }
    }


    @Override
    public void store(String topic, SagaCommand command, String className) {
        try {
            OutboxMessage message = OutboxMessage.builder()
                    .id(AppUtils.generateUUIDv7())
                    .messageId(command.getMessageId())
                    .processed(Boolean.FALSE)
                    .processedAt(null)
                    .type(MessageType.COMMAND)
                    .topic(topic)
                    .payload(objectMapper.writeValueAsString(command))
                    .className(className)
                    .build();

            outboxMessageRepository.save(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize command payload", e);
        }
    }

    @Override
    public void publishMessage(OutboxMessage message) {
        /// 1. publish message -> I/O bound task.
        /// 2. Update DB -> round trip -> I/O bound task.

        if (!StringUtils.hasText(message.getClassName())) {
            log.error("ClassName is missing for Command message with ID: {}", message.getId());
            return;
        }
        String className = message.getClassName();
        if (message.getType().equals(MessageType.COMMAND)) {
            try {
                if (className.contains("ReserveProductCommand")) {
                    ReserveProductCommand command = objectMapper.readValue(message.getPayload(), ReserveProductCommand.class);
                    command.setMessageId(message.getId());
                    CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(message.getTopic(), command.getSagaId(), command);
                    this.handleSendResult(future, message);
                } else if (className.contains("ProcessPaymentCommand")) {
                    ProcessPaymentCommand ppcCommand = objectMapper.readValue(message.getPayload(), ProcessPaymentCommand.class);
                    ppcCommand.setMessageId(message.getId());
                    this.handleSendResult(
                            kafkaTemplate.send(message.getTopic(), ppcCommand.getSagaId(), ppcCommand),
                            message
                    );
                } else if (className.contains("RefundProcessedPaymentCommand")) {
                    RefundProcessedPaymentCommand rppCommand = objectMapper.readValue(message.getPayload(), RefundProcessedPaymentCommand.class);
                    rppCommand.setMessageId(message.getId());
                    this.handleSendResult(
                            kafkaTemplate.send(message.getTopic(), rppCommand.getSagaId(), rppCommand),
                            message
                    );
                } else if (className.contains("ReleaseProductCommand")) {
                    ReleaseProductCommand rpcCommand = objectMapper.readValue(message.getPayload(), ReleaseProductCommand.class);
                    rpcCommand.setMessageId(message.getId());
                    this.handleSendResult(
                            kafkaTemplate.send(message.getTopic(), rpcCommand.getSagaId(), rpcCommand),
                            message
                    );
                } else {
                    log.error("Không thể xác định loại Command để publish. Message ID: {}, ClassName: {}", message.getId(), message.getClassName());
                }
            } catch (Exception e) {
                throw new RuntimeException("Có lỗi xảy ra khi thực hiện publish Command", e);
            }
        } else if (message.getType().equals(MessageType.EVENT)) {
            try {
                if (className.contains("OrderCompletionFailedEvent")) {
                    OrderCompletionFailedEvent orderCompletionFailedEvent = objectMapper.readValue(message.getPayload(), OrderCompletionFailedEvent.class);
                    orderCompletionFailedEvent.setMessageId(message.getId());
                    this.handleSendResult(
                            kafkaTemplate.send(message.getTopic(), orderCompletionFailedEvent.getSagaId(), orderCompletionFailedEvent),
                            message
                    );
                } else {
                    log.error("Không thể xác định loại message để publish. Message ID: {}, ClassName: {}", message.getId(), message.getClassName());
                }
            } catch (Exception e) {
                throw new RuntimeException("Có lỗi xảy ra khi thực hiện publish Event", e);
            }
        }
    }

    private void handleSendResult(CompletableFuture<SendResult<String, Object>> future, OutboxMessage message) {
        future.whenComplete((result, ex) -> {
            if (Objects.isNull(ex)) {
                message.setProcessed(Boolean.TRUE);
                message.setProcessedAt(Instant.now());
                outboxMessageRepository.save(message);
            } else {
                log.error("Có lỗi xảy ra khi gửi message với ID {}: {}", message.getId(), ex.getMessage());
            }
        });
    }

    @Override
    public List<OutboxMessage> findTopNUnprocessedMessage(int N) {
        return outboxMessageRepository.findTopNUnprocessedMessage(N);
    }
}