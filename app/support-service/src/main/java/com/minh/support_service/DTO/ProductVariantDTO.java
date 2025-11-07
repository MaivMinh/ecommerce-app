package com.minh.support_service.DTO;


import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantDTO {
    @NotEmpty(message = "ID cannot be empty")
    private String id;
    private String name;
    private String slug;
    private String size;
    private String colorName;
    private String colorHex;
    private String cover;
    private Double price;
    private Double originalPrice;
}
