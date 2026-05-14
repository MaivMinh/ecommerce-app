package com.minh.product_service.exception;


import com.minh.common.commands.ReleaseProductCommand;
import com.minh.common.commands.ReserveProductCommand;
import com.minh.common.events.ProductReservationFailedEvent;
import com.minh.common.exception.BusinessException;
import com.minh.common.kafka.KafkaTopics;
import com.minh.product_service.outbox.OutboxMessageService;
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
    public DefaultErrorHandler productKafkaErrorHandler(KafkaTemplate<String, Object> kafkaTemplate, OutboxMessageService outboxMessageService) {

        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(3);
        backOff.setInitialInterval(2000L);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(15000L);

        return new DefaultErrorHandler(
                (record, exception) -> {
                    if (!(exception instanceof BusinessException)) {
                        Object message = record.value();
                        if (message instanceof ReserveProductCommand command) {
                            log.error("Lỗi khi đặt chỗ sản phẩm cho đơn hàng {}: {}", command.getOrderId(), exception.getMessage());
                            ProductReservationFailedEvent event = ProductReservationFailedEvent.builder()
                                    .orderId(command.getOrderId())
                                    .username(command.getUsername())
                                    .errorMsg(exception.getMessage())
                                    .build();
                            event.setMessageId(command.getMessageId());
                            outboxMessageService.store(KafkaTopics.PRODUCT_RESERVATION_FAILED, event, event.getClass().getName());
                        } else if (message instanceof ReleaseProductCommand command) {
                            log.info("Lỗi khi thực hiện giải phóng sản phẩm đã đặt chỗ cho đơn hàng {}: {}", command.getOrderId(), exception.getMessage());
                            outboxMessageService.store(record.topic(), command, command.getClass().getName());
                        } else {
                            log.error("Received unknown message type: {}", message.getClass().getName());
                        }
                    }
                },
                backOff
        );
    }
}
