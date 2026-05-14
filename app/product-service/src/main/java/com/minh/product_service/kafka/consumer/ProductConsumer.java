package com.minh.product_service.kafka.consumer;

import com.minh.common.commands.ReleaseProductCommand;
import com.minh.common.commands.ReserveProductCommand;
import com.minh.common.kafka.KafkaTopics;
import com.minh.product_service.service.ReserveProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductConsumer {
    private final ReserveProductService service;

    @KafkaListener(
            topics = KafkaTopics.PRODUCT_RESERVE,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleReserveProductCommand(ReserveProductCommand command) {
        service.reserveProduct(command);
    }


    @KafkaListener(
            topics = KafkaTopics.PRODUCT_RELEASE,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleReleaseProductCommand(ReleaseProductCommand command) {
        service.releaseReservedProduct(command);
    }
}
