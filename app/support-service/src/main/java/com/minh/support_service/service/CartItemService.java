package com.minh.support_service.service;

import com.minh.common.response.ResponseData;
import com.minh.support_service.DTO.CartItemDTO;
import jakarta.validation.Valid;

public interface CartItemService {
    ResponseData createCartItem(CartItemDTO cartItemDTO);

    ResponseData updateCartItem(@Valid CartItemDTO cartItemDTO);

    ResponseData deleteCartItem(String id);

    ResponseData findCartItemOfUser();
}
