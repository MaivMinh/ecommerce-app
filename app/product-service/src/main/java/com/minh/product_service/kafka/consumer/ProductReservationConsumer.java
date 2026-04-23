package com.minh.product_service.kafka.consumer;

import com.minh.common.commands.ReleaseProductCommand;
import com.minh.common.commands.ReserveProductCommand;
import com.minh.common.kafka.KafkaTopics;
import com.minh.product_service.service.ReserveProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductReservationConsumer {
    private final ReserveProductService service;

    @KafkaListener(
            topics = KafkaTopics.PRODUCT_RESERVE,
            groupId = "product-service",
            containerFactory = "kafkaListenerContainerFactory"   /// Cần phải khai báo containerFactory, trong class này đang chứa DefaultErrorHandler để xử lý lỗi khi consume message. Nếu không khai báo thì KafkaListener sẽ sử dụng containerFactory mặc định, mà trong đó không có DefaultErrorHandler. Cụ thể là DefaultErrorHandler này sẽ có Backoff với maxAttemps khoảng 10 lần. Nếu muốn kiểm tra thì chỉ cần xóa containerFactory đi sau đó chạy lại một flow lỗi.
            /// Lưu ý, Kafka Error Handler sẽ dùng để catch lại những lỗi được throw ra trong quá trình consume message hoặc khi các hàm trong service throw ra.
            /// Về cách hoạt động nó khác giống với GlobalExceptionHandler của Spring Boot.
    )
    @Transactional
    public void handleReserveProductCommand(ReserveProductCommand command) {
        service.reserveProduct(command);
    }


    @KafkaListener(
            topics = KafkaTopics.PRODUCT_RELEASE,
            groupId = "product-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void handleReleaseProductCommand(ReleaseProductCommand command) {
        service.releaseReservedProduct(command);
    }
}
