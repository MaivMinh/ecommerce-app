package com.minh.product_service.service.impl;

import com.minh.common.constants.ErrorCode;
import com.minh.common.constants.ResponseMessages;
import com.minh.common.exception.ResourceNotFoundException;
import com.minh.common.message.MessageCommon;
import com.minh.common.response.ResponseData;
import com.minh.product_service.command.events.CategoryCreatedEvent;
import com.minh.product_service.command.events.CategoryDeletedEvent;
import com.minh.product_service.command.events.CategoryUpdatedEvent;
import com.minh.product_service.dto.CategoryDTO;
import com.minh.product_service.entity.Category;
import com.minh.product_service.query.queries.FindAllCategoriesQuery;
import com.minh.product_service.query.queries.FindCategoriesQuery;
import com.minh.product_service.query.queries.FindCategoryBySlug;
import com.minh.product_service.query.queries.SearchCategoriesByNameQuery;
import com.minh.product_service.service.CategoryService;
import com.minh.product_service.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final MessageCommon messageCommon;
    private final ModelMapper modelMapper;

    private static String generateSlug(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        // Convert to lowercase
        String slug = name.toLowerCase();
        // Handle Vietnamese accents and other diacritical marks
        slug = java.text.Normalizer.normalize(slug, java.text.Normalizer.Form.NFD);
        // Replace spaces with hyphens
        slug = slug.replaceAll("\\s+", "-");
        // Remove special characters but keep normalized characters
        slug = slug.replaceAll("[^\\p{ASCII}]", "");
        slug = slug.replaceAll("[^a-z0-9-]", "");
        // Replace multiple hyphens with a single one
        slug = slug.replaceAll("-+", "-");
        // Remove leading and trailing hyphens
        slug = slug.replaceAll("^-|-$", "");
        return slug;
    }

    @Override
    public ResponseData getCategoryById(String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            return ResponseData.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message(messageCommon.getMessage(ErrorCode.Category.NOT_FOUND, categoryId))
                    .build();
        }
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message(ResponseMessages.SUCCESS)
                .data(modelMapper.map(category, CategoryDTO.class))
                .build();
    }

    @Override
    public ResponseData findAllCategories(FindAllCategoriesQuery query) {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());

        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message(ResponseMessages.SUCCESS)
                .data(categoryDTOs)
                .build();
    }

    @Override
    public ResponseData findCategoryBySlug(FindCategoryBySlug query) {
        if (!StringUtils.hasText(query.getSlug())) {
            throw new ResourceNotFoundException(
                    HttpStatus.NOT_FOUND.value(),
                    messageCommon.getMessage(ErrorCode.Category.SLUG_NOT_FOUND, query.getSlug())
            );
        }

        Category category = categoryRepository.findBySlug(query.getSlug());
        if (category == null) {
            return ResponseData.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message(messageCommon.getMessage(ErrorCode.Category.SLUG_NOT_FOUND, query.getSlug()))
                    .build();
        }

        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message(ResponseMessages.SUCCESS)
                .data(modelMapper.map(category, CategoryDTO.class))
                .build();
    }

    @Override
    public ResponseData searchCategoriesByName(SearchCategoriesByNameQuery query) {
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase(query.getName());
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());

        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message(ResponseMessages.SUCCESS)
                .data(categoryDTOs)
                .build();
    }

    @Override
    public void createCategory(CategoryCreatedEvent event) {
        Category category = modelMapper.map(event, Category.class);
        category.setSlug(generateSlug(event.getName()));
        categoryRepository.save(category);
    }

    @Override
    public void updateCategory(CategoryUpdatedEvent event) {
        Category category = categoryRepository.findById(event.getId()).orElseThrow(
                () -> new RuntimeException(
                        messageCommon.getMessage(ErrorCode.Category.NOT_FOUND, event.getId())
                )
        );
        modelMapper.map(event, category);
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(CategoryDeletedEvent event) {
        Category category = categoryRepository.findById(event.getId()).orElseThrow(
                () -> new RuntimeException(messageCommon.getMessage(ErrorCode.Category.NOT_FOUND, event.getId()))
        );
        categoryRepository.delete(category);
    }

    @Override
    public ResponseData findCategories(FindCategoriesQuery query) {
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());
        List<Category> categories = categoryRepository.findAll(pageable).getContent();
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message(ResponseMessages.SUCCESS)
                .data(categoryDTOs)
                .build();
    }
}
