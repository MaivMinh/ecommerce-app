package com.minh.product_service.functions;

import com.minh.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;


@Component
@RequiredArgsConstructor
public class ProductFunctions {
    private final ProductService productService;

    @Bean
    public Consumer<String> handleProductUpdatedEvent() {
        return productService::handleProductUpdatedEvent;
    }
}
