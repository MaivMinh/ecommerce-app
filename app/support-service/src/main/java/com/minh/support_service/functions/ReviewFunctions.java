package com.minh.support_service.functions;

import com.minh.common.functions.input.OrderCreatedEventInput;
import com.minh.common.utils.AppUtils;
import com.minh.support_service.DTO.PurchasedProductDto;
import com.minh.support_service.service.PurchasedProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewFunctions {
    private final PurchasedProductService purchasedOrderService;

    @Bean
    public Consumer<OrderCreatedEventInput> handleOrderCreated() {
        return event -> {
            log.info("Nhận được sự kiện đặt hàng thành công: {}", event);
            if (!validateOrderCreatedEvent(event)) {
                log.error("Dữ liệu sự kiện đặt hàng thành công không hợp lệ");
                return;
            }

            PurchasedProductDto dto = new PurchasedProductDto();
            dto.setId(AppUtils.generateUUIDv7());
            dto.setProductId(event.getProductId());
            dto.setUsername(event.getUsername());
            purchasedOrderService.savePurchasedOrder(dto);
        };
    }

    private Boolean validateOrderCreatedEvent(OrderCreatedEventInput event) {
        log.info("username: {}, productId: {}", event.getUsername(), event.getProductId());
        // Implement validation logic here
        return Objects.nonNull(event) && StringUtils.hasText(event.getUsername()) && StringUtils.hasText(event.getProductId());
    }
}
