package com.minh.support_service.service.impl;

import com.minh.common.constants.ErrorCode;
import com.minh.common.constants.ResponseMessages;
import com.minh.common.message.MessageCommon;
import com.minh.common.response.ResponseData;
import com.minh.common.utils.AppUtils;
import com.minh.support_service.DTO.CartItemDTO;
import com.minh.support_service.DTO.ProductVariantDTO;
import com.minh.support_service.entity.Cart;
import com.minh.support_service.entity.CartItem;
import com.minh.support_service.grpc.client.ProductGrpcClient;
import com.minh.support_service.repository.CartItemProductVariantProjection;
import com.minh.support_service.repository.CartItemRepository;
import com.minh.support_service.repository.CartRepository;
import com.minh.support_service.service.CartItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import product_service.CartItemField;
import product_service.FindProductVariantsByIdsRequest;
import product_service.FindProductVariantsByIdsResponse;
import product_service.ProductVariant;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final MessageCommon messageCommon;
    private final ProductGrpcClient productGrpcClient;


    /// Hàm thực hiện tạo một mục trong giỏ hàng của người dùng
    public ResponseData createCartItem(CartItemDTO cartItemDTO) {
        String username = AppUtils.getUsername();
        try {
            Cart saved = cartRepository.findByUsername(username);
            if (saved == null) {
                /// Tạo mới cart tương ứng với current user.
                Cart cart = new Cart();
                cart.setId(UUID.randomUUID().toString());
                cart.setUsername(username);
                saved = cartRepository.save(cart);
            }

            /// Check xem đã có productVariantId trong cartId chưa. Nếu đã có thì chỉ việc update lại số lượng của Item sẵn có này.
            CartItem existingItem = cartItemRepository.checkHasTheSameProductVariantInCartId(saved.getId(), cartItemDTO.getProductVariantDTO().getId()).orElse(null);

            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + cartItemDTO.getQuantity());
                cartItemRepository.save(existingItem);
                return ResponseData.builder()
                        .status(HttpStatus.OK.value())
                        .message("Cart item updated successfully")
                        .data(null)
                        .build();
            }

            /// Nếu chưa thì tạo mới Cart Item.
            CartItem cartItem = new CartItem();
            cartItem.setId(UUID.randomUUID().toString());
            cartItem.setCartId(saved.getId());
            cartItem.setQuantity(cartItemDTO.getQuantity());
            cartItem.setProductVariantId(cartItemDTO.getProductVariantDTO().getId());
            cartItemRepository.save(cartItem);
            return ResponseData.builder()
                    .status(HttpStatus.CREATED.value())
                    .message("Success")
                    .data(null)
                    .build();
        } catch (Exception e) {
            log.error("Error creating cart item: {}", e.getMessage());
            throw new RuntimeException("Error creating cart item: " + e.getMessage());
        }
    }

    public ResponseData updateCartItem(CartItemDTO cartItemDTO) {
        CartItem item = cartItemRepository.findById(cartItemDTO.getId()).orElse(null);
        if (Objects.isNull(item)) {
            return ResponseData.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message(messageCommon.getMessage(ErrorCode.CartItem.NOT_FOUND, cartItemDTO.getId()))
                    .data(null)
                    .build();
        }


        item.setQuantity(cartItemDTO.getQuantity());
        item.setProductVariantId(cartItemDTO.getProductVariantDTO().getId());
        cartItemRepository.save(item);
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message(ResponseMessages.SUCCESS)
                .data(null)
                .build();
    }

    public ResponseData deleteCartItem(String id) {
        CartItem item = cartItemRepository.findById(id).orElse(null);
        if (Objects.isNull(item)) {
            return ResponseData.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message(messageCommon.getMessage(ErrorCode.CartItem.NOT_FOUND, id))
                    .data(null)
                    .build();
        }
        cartItemRepository.delete(item);
        return ResponseData.builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message(ResponseMessages.SUCCESS)
                .data(null)
                .build();
    }

    @Override
    public ResponseData findCartItemOfUser() {
        String username = AppUtils.getUsername();

        try {

            Cart cart = cartRepository.findByUsername(username);
            if (cart == null) {
                return ResponseData.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .message(messageCommon.getMessage(ErrorCode.Cart.NOT_FOUND_WITH_USER_ID, username))
                        .data(null)
                        .build();
            }

            List<CartItemProductVariantProjection> projections = cartItemRepository.findAllProductVariantIdsByCartId(cart.getId());

            FindProductVariantsByIdsRequest request = FindProductVariantsByIdsRequest.newBuilder()
                    .addAllCartItemField(
                            projections.stream().map(
                                            projection -> CartItemField.newBuilder()
                                                    .setCartItemId(projection.getCartItemId())
                                                    .setProductVariantId(projection.getProductVariantId())
                                                    .build())
                                    .toList()
                    ).build();

            FindProductVariantsByIdsResponse response = productGrpcClient.findProductVariantsByIds(request);
            if (response.getStatus() != HttpStatus.OK.value() || response.getProductVariantList().isEmpty()) {
                return ResponseData.builder()
                        .status(response.getStatus())
                        .message(response.getMessage())
                        .data(null)
                        .build();
            }

            Map<String, ProductVariant> map = new HashMap<>();
            List<String> cartItemIds = new ArrayList<>();
            for (ProductVariant pv : response.getProductVariantList()) {
                map.put(pv.getId(), pv);
                cartItemIds.add(pv.getCartItemId());
            }
            List<CartItemDTO> cartItemDTOs = new ArrayList<>();

            List<CartItem> cartItems = cartItemRepository.findAllByIds(cartItemIds);
            cartItems.forEach(cartItem -> {
                ProductVariant pv = map.get(cartItem.getProductVariantId());
                if (pv != null) {
                    CartItemDTO cartItemDTO = new CartItemDTO();
                    ProductVariantDTO productVariantDTO = new ProductVariantDTO();
                    productVariantDTO.setId(pv.getId());
                    productVariantDTO.setName(pv.getName());
                    productVariantDTO.setSlug(pv.getSlug());
                    productVariantDTO.setColorHex(pv.getColorHex());
                    productVariantDTO.setColorName(pv.getColorName());
                    productVariantDTO.setCover(pv.getCover());
                    productVariantDTO.setOriginalPrice(pv.getOriginalPrice());
                    productVariantDTO.setPrice(pv.getPrice());
                    productVariantDTO.setSize(pv.getSize());
                    cartItemDTO.setId(cartItem.getId());
                    cartItemDTO.setQuantity(cartItem.getQuantity());
                    cartItemDTO.setProductVariantDTO(productVariantDTO);
                    cartItemDTO.setQuantity(cartItem.getQuantity());
                    cartItemDTO.setProductId(pv.getProductId());
                    cartItemDTOs.add(cartItemDTO);
                }
            });

            double subTotal = 0;
            for (CartItemDTO item : cartItemDTOs) {
                subTotal += item.getProductVariantDTO().getPrice() * item.getQuantity();
            }
            Map<String, Object> data = new HashMap<>();
            data.put("items", cartItemDTOs);
            data.put("subTotal", subTotal);

            return ResponseData.builder()
                    .status(HttpStatus.OK.value())
                    .message("Success")
                    .data(data)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
