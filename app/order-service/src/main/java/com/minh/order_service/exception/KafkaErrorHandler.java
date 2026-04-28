package com.minh.order_service.exception;


import com.minh.common.commands.SagaCommand;
import com.minh.common.events.SagaEvent;
import com.minh.common.kafka.KafkaTopics;
import com.minh.order_service.outbox.OutboxMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaErrorHandler {

    @Bean
    public DefaultErrorHandler orderKafkaErrorHandler(KafkaTemplate<String, Object> kafkaTemplate, OutboxMessageService outboxMessageService) {

        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(3);
        backOff.setInitialInterval(2000L);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(15000L);

        return new DefaultErrorHandler(
                (record, ex) -> {
                    if (record.value() instanceof SagaCommand command) {
                        outboxMessageService.store(KafkaTopics.GLOBAL_TECHNICAL_DLT, command, command.getClass().getName());
                        log.info("Stored failed command in outbox: {}", command);
                    } else if (record.value() instanceof SagaEvent event) {
                        outboxMessageService.store(KafkaTopics.GLOBAL_TECHNICAL_DLT, event, event.getClass().getName());
                        log.info("Stored failed event in outbox: {}", event);
                    }
                },
                backOff
        );
    }
}
