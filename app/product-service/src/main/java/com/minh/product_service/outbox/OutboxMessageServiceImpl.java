package com.minh.product_service.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.common.commands.ReleaseProductCommand;
import com.minh.common.commands.SagaCommand;
import com.minh.common.enums.MessageType;
import com.minh.common.events.ProductReleasedEvent;
import com.minh.common.events.ProductReservationFailedEvent;
import com.minh.common.events.ProductReservedEvent;
import com.minh.common.events.SagaEvent;
import com.minh.common.utils.AppUtils;
import com.minh.product_service.entity.OutboxMessage;
import com.minh.product_service.repository.OutboxMessageRepository;
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
            log.error("ClassName is missing for message with ID: {}", message.getId());
            return;
        }
        String className = message.getClassName();
        if (message.getType().equals(MessageType.COMMAND)) {
            try {
                if (className.contains("ReleaseProductCommand")) {
                    ReleaseProductCommand command = objectMapper.readValue(message.getPayload(), ReleaseProductCommand.class);
                    command.setMessageId(message.getId());
                    CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(message.getTopic(), command.getSagaId(), command);
                    this.handleSendResult(future, message);
                } else {
                    log.error("Không thể xác định loại message để publish. Message ID: {}, ClassName: {}", message.getId(), message.getClassName());
                }
            } catch (Exception e) {
                throw new RuntimeException("Có lỗi xảy ra khi thực hiện publish Command", e);
            }
        } else if (message.getType().equals(MessageType.EVENT)) {
            try {
                if (className.contains("ProductReservationFailedEvent")) {
                    ProductReservationFailedEvent event = objectMapper.readValue(message.getPayload(), ProductReservationFailedEvent.class);
                    event.setMessageId(message.getId());
                    CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(message.getTopic(), event.getSagaId(), event);
                    this.handleSendResult(future, message);
                } else if (className.contains("ProductReservedEvent")) {
                    ProductReservedEvent event = objectMapper.readValue(message.getPayload(), ProductReservedEvent.class);
                    event.setMessageId(message.getId());
                    CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(message.getTopic(), event.getSagaId(), event);
                    this.handleSendResult(future, message);
                } else if (className.contains("ProductReleasedEvent")) {
                    ProductReleasedEvent event = objectMapper.readValue(message.getPayload(), ProductReleasedEvent.class);
                    event.setMessageId(message.getId());
                    CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(message.getTopic(), event.getSagaId(), event);
                    this.handleSendResult(future, message);
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

    @Override
    public boolean isMessageProcessed(String messageId) {
        return outboxMessageRepository.existsByMessageId(messageId);
    }
}