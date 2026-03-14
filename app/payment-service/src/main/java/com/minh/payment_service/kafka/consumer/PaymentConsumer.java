package com.minh.payment_service.kafka.consumer;

import com.minh.common.commands.ProcessPaymentCommand;
import com.minh.common.commands.RefundProcessedPaymentCommand;
import com.minh.common.kafka.KafkaTopics;
import com.minh.payment_service.enums.PaymentProvider;
import com.minh.payment_service.factory.PaymentStrategyFactory;
import com.minh.payment_service.service.PaymentProcessingService;
import com.minh.payment_service.service.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentConsumer {
    private final PaymentProcessingService service;
    private final PaymentStrategyFactory factory;

    private PaymentStrategy getPaymentStrategy(String paymentMethod) {
        PaymentProvider provider = PaymentProvider.valueOf(paymentMethod);
        return factory.getPaymentStrategy(provider);
    }

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_PROCESS,
            groupId = "payment-service",
            containerFactory = "kafkaListenerSagaCommandContainerFactory"   /// Cần phải khai báo containerFactory, trong class này đang chứa DefaultErrorHandler để xử lý lỗi khi consume message. Nếu không khai báo thì KafkaListener sẽ sử dụng containerFactory mặc định, mà trong đó không có DefaultErrorHandler. Cụ thể là DefaultErrorHandler này sẽ có Backoff với maxAttemps khoảng 10 lần. Nếu muốn kiểm tra thì chỉ cần xóa containerFactory đi sau đó chạy lại một flow lỗi.
            /// Lưu ý, Kafka Error Handler sẽ dùng để catch lại những lỗi được throw ra trong quá trình consume message hoặc khi các hàm trong service throw ra.
            /// Về cách hoạt động nó khác giống với GlobalExceptionHandler của Spring Boot.
    )
    @Transactional
    public void handleProcessPaymentCommand(ProcessPaymentCommand command) {
        log.info("Received ProcessPaymentCommand for sagaId: {}", command.getSagaId());
        PaymentStrategy strategy = this.getPaymentStrategy(command.getPaymentMethod());
        service.processPayment(command, strategy);
    }


    @KafkaListener(
            topics = KafkaTopics.PAYMENT_REFUND,
            groupId = "payment-service",
            containerFactory = "kafkaListenerSagaCommandContainerFactory"
    )
    @Transactional
    public void handleRefundPaymentCommand(RefundProcessedPaymentCommand command) {
        log.info("Received RefundPaymentCommand for sagaId: {}", command.getSagaId());
        PaymentStrategy strategy = this.getPaymentStrategy(command.getPaymentMethod());
        service.refundPayment(command, strategy);
    }
}
