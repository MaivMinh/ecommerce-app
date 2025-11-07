package com.minh.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {
    private String id;
    private String parentId;
    @NotBlank
    private String name;
    private String description;
    private String slug;
    @NotBlank
    private String image;
    private Boolean isDeleted;
}
