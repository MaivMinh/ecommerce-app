package com.minh.product_service.kafka.consumer;

import com.minh.common.commands.ReleaseProductCommand;
import com.minh.common.commands.ReserveProductCommand;
import com.minh.common.kafka.KafkaTopics;
import com.minh.product_service.service.ReserveProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductReservationConsumer {
    private final ReserveProductService service;

    @KafkaListener(
            topics = KafkaTopics.PRODUCT_RESERVE,
            groupId = "product-service"
    )
    @Transactional
    public void handleReserveProductCommand(ReserveProductCommand command) {
        service.reserveProduct(command);
    }

    @KafkaListener(
            topics = KafkaTopics.PRODUCT_RELEASE,
            groupId = "product-service"
    )
    @Transactional
    public void handleReleaseProductCommand(ReleaseProductCommand command) {
        service.releaseReservedProduct(command);
    }
}
