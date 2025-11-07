package com.minh.product_service.command.controller;

import com.minh.common.response.ResponseData;
import com.minh.product_service.command.commands.CreateProductCommand;
import com.minh.product_service.command.commands.DeleteProductCommand;
import com.minh.product_service.command.commands.UpdateProductCommand;
import com.minh.product_service.dto.ProductDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@RequestMapping(value = "/api/products")
@Validated
@RestController
public class ProductCommandController {
    private final CommandGateway commandGateway;

    @PostMapping(value = "")
    public ResponseEntity<ResponseData> createProduct(@RequestBody @Valid ProductDTO dto) {
        CreateProductCommand command = CreateProductCommand.builder()
                .id(UUID.randomUUID().toString())
                .name(dto.getName())
                .slug(dto.getSlug())
                .description(dto.getDescription())
                .cover(dto.getCover())
                .images(dto.getImages())
                .price(dto.getPrice())
                .originalPrice(dto.getOriginalPrice())
                .status(dto.getStatus())
                .isFeatured(dto.getIsFeatured())
                .isNew(dto.getIsNew())
                .isBestseller(dto.getIsBestseller())
                .categoryId(dto.getCategoryId())
                .productVariants(dto.getProductVariants())
                .build();

        commandGateway.sendAndWait(command, 20000, TimeUnit.MILLISECONDS);
        return ResponseEntity.ok(null);
    }

    @PutMapping(value = "")
    public ResponseEntity<ResponseData> updateProduct(@RequestBody @Valid ProductDTO dto) {
        UpdateProductCommand command = UpdateProductCommand.builder()
                .id(dto.getId())
                .name(dto.getName())
                .slug(dto.getSlug())
                .description(dto.getDescription())
                .cover(dto.getCover())
                .images(dto.getImages())
                .price(dto.getPrice())
                .originalPrice(dto.getOriginalPrice())
                .status(dto.getStatus())
                .isFeatured(dto.getIsFeatured())
                .isNew(dto.getIsNew())
                .isBestseller(dto.getIsBestseller())
                .categoryId(dto.getCategoryId())
                .productVariants(dto.getProductVariants())
                .build();

        commandGateway.sendAndWait(command, 20000, TimeUnit.MILLISECONDS);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping(value = "/{productId}")
    public ResponseEntity<ResponseData> deleteProduct(@PathVariable(name = "productId") String productId) {
        DeleteProductCommand command = DeleteProductCommand.builder()
                .id(productId)
                .build();

        commandGateway.sendAndWait(command, 20000, TimeUnit.MILLISECONDS);
        return ResponseEntity.ok(null);
    }
}
