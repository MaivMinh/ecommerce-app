package com.minh.product_service.service.impl;

import com.minh.common.commands.ReleaseProductCommand;
import com.minh.common.commands.ReserveProductCommand;
import com.minh.common.events.ProductReleasedEvent;
import com.minh.common.events.ProductReservationFailedEvent;
import com.minh.common.events.ProductReservedEvent;
import com.minh.common.kafka.KafkaTopics;
import com.minh.product_service.dto.ProductVariantDTO;
import com.minh.product_service.entity.ReserveProduct;
import com.minh.product_service.enums.ReserveProductStatus;
import com.minh.product_service.repository.ReserveProductRepository;
import com.minh.product_service.service.ProductVariantService;
import com.minh.product_service.service.ReserveProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveProductServiceImpl implements ReserveProductService {
    private final ReserveProductRepository reserveProductRepository;
    private final ProductVariantService productVariantService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public void reserveProduct(ReserveProductCommand command) {
        try {
            if (CollectionUtils.isEmpty(command.getReserveProductItems())) {
                return;
            }
            List<ReserveProduct> reserveProducts = new ArrayList<>();
            command.getReserveProductItems().forEach(item -> {
                ReserveProduct reserveProduct = new ReserveProduct();
                productVariantService.decreaseProductVariantQuantity(item.getProductVariantId(), item.getQuantity());
                reserveProduct.setId(UUID.randomUUID().toString());
                reserveProduct.setOrderId(command.getOrderId());
                reserveProduct.setProductVariantId(item.getProductVariantId());
                reserveProduct.setQuantity(item.getQuantity());
                reserveProduct.setStatus(ReserveProductStatus.locking);
                reserveProducts.add(reserveProduct);
            });
            reserveProductRepository.saveAll(reserveProducts);
            ProductReservedEvent event = ProductReservedEvent.builder()
                    .orderId(command.getOrderId())
                    .paymentMethod(command.getPaymentMethod())
                    .total(command.getTotal())
                    .currency(command.getCurrency())
                    .username(command.getUsername())
                    .build();
            event.setSagaId(command.getSagaId());
            event.setTimestamp(command.getTimestamp());

            kafkaTemplate.send(KafkaTopics.PRODUCT_RESERVED, command.getSagaId(), event);
        } catch (RuntimeException e) {
            log.error("Lỗi khi đặt chỗ sản phẩm cho đơn hàng {}: {}", command.getOrderId(), e.getMessage());
            ProductReservationFailedEvent event = ProductReservationFailedEvent.builder()
                    .orderId(command.getOrderId())
                    .username(command.getUsername())
                    .errorMsg(e.getMessage())
                    .build();
            kafkaTemplate.send(KafkaTopics.PRODUCT_RESERVATION_FAILED, command.getSagaId(), event);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
    public void releaseReservedProduct(ReleaseProductCommand command) {
        String orderId = command.getOrderId();
        List<ReserveProduct> reserveProducts = reserveProductRepository.findAllByOrderId(orderId);
        if (reserveProducts.isEmpty()) {
            throw new RuntimeException("Không có sản phẩm nào cho đơn hàng này: " + orderId);
        }
        reserveProducts.forEach(reserveProduct -> {
            /// Trả lại số lượng sản phẩm vào kho.
            ProductVariantDTO variant = productVariantService.findById(reserveProduct.getProductVariantId());
            variant.setQuantity(variant.getQuantity() + reserveProduct.getQuantity());
            productVariantService.updateProductVariant(variant);
            /// Xoá bản ghi đặt chỗ sản phẩm.
            reserveProduct.setStatus(ReserveProductStatus.failed);
            reserveProductRepository.save(reserveProduct);
        });
        ProductReleasedEvent event = ProductReleasedEvent.builder()
                .orderId(orderId)
                .build();
        event.setSagaId(command.getSagaId());
        event.setTimestamp(command.getTimestamp());

        kafkaTemplate.send(KafkaTopics.PRODUCT_RELEASED, command.getSagaId(), event);
    }
}
