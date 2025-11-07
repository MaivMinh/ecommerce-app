package com.minh.support_service.controller;

import com.minh.common.response.ResponseData;
import com.minh.support_service.DTO.CartItemDTO;
import com.minh.support_service.service.CartItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping(value = "/api/carts", produces = {"application/json"})
@RequiredArgsConstructor
public class CartController {
    private final CartItemService cartService;

    /// Hàm thực hiện tạ một mục trong giỏ hàng của người dùng
    /// DONE
    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/cart-items")
    public ResponseEntity<ResponseData> createCartItem(@RequestBody @Valid CartItemDTO cartItemDTO) {
        ResponseData response = cartService.createCartItem(cartItemDTO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /// Hàm thực hiện cập nhật một mục trong giỏ hàng của người dùng
    /// DONE
    @PreAuthorize("hasRole('USER')")
    @PutMapping(value = "/cart-items/{id}")
    public ResponseEntity<ResponseData> updateCartItem(@PathVariable(name = "id") String id, @RequestBody @Valid CartItemDTO cartItemDTO) {
        cartItemDTO.setId(id);
        ResponseData response = cartService.updateCartItem(cartItemDTO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /// Hàm thực hiện xóa một mục trong giỏ hàng của người dùng
    /// DONE
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping(value = "/cart-items/{id}")
    public ResponseEntity<ResponseData> deleteCartItem(@PathVariable(name = "id") String id) {
        ResponseData response = cartService.deleteCartItem(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT.value()).body(response);
    }

    /// Hàm thực hiện lấy thông tin giỏ hàng, bao gồm thông tin chung cũng như các mục trong giỏ hàng của người dùng.
    /// DONE.
    @PreAuthorize("hasRole('USER')")
    @GetMapping(value = "/cart-items")
    public ResponseEntity<ResponseData> findCartItemOfUser() {
        ResponseData response = cartService.findCartItemOfUser();
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}