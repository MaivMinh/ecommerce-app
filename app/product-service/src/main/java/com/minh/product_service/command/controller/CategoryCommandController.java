package com.minh.product_service.command.controller;

import com.minh.common.response.ResponseData;
import com.minh.product_service.command.commands.CreateCategoryCommand;
import com.minh.product_service.command.commands.DeleteCategoryCommand;
import com.minh.product_service.command.commands.UpdateCategoryCommand;
import com.minh.product_service.dto.CategoryDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/categories")
@Validated
public class CategoryCommandController {
    private final CommandGateway commandGateway;

    @PostMapping(value = "")
    public ResponseEntity<ResponseData> createCategory(@RequestBody @Valid CategoryDTO categoryDTO) {
        CreateCategoryCommand command = CreateCategoryCommand.builder()
                .id(UUID.randomUUID().toString())
                .parentId(categoryDTO.getParentId())
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .image(categoryDTO.getImage())
                .build();

        /// dispatch the command to Aggregate (command handler).
        commandGateway.sendAndWait(command, 20000, TimeUnit.MILLISECONDS);
        return ResponseEntity.ok(null);
    }

    @PutMapping(value = "")
    public ResponseEntity<ResponseData> updateCategory(@RequestBody @Valid CategoryDTO categoryDTO) {
        UpdateCategoryCommand command = UpdateCategoryCommand.builder()
                .id(categoryDTO.getId())
                .parentId(categoryDTO.getParentId())
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .image(categoryDTO.getImage())
                .slug(categoryDTO.getSlug())
                .build();

        commandGateway.sendAndWait(command, 20000, TimeUnit.MILLISECONDS);
        return ResponseEntity.ok(new ResponseData(categoryDTO));
    }


    @DeleteMapping(value = "/{categoryId}")
    public ResponseEntity<ResponseData> deleteCategory(@PathVariable(name = "categoryId") String categoryId) {
        DeleteCategoryCommand command = DeleteCategoryCommand.builder()
                .id(categoryId)
                .build();

        commandGateway.sendAndWait(command, 20000, TimeUnit.MILLISECONDS);
        return ResponseEntity.ok(null);
    }
}
