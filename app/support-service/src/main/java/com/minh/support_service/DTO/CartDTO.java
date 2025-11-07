package com.minh.support_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CartDTO {
    private String id;
    private String username;
    private List<CartItemDTO> cartItems;
    private Double subtotal;
}
