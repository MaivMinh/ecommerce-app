package com.minh.product_service.service.impl;

import com.minh.common.commands.ReserveProductConfirmedEvent;
import com.minh.common.constants.ErrorCode;
import com.minh.common.events.ProductReservedRollbackedEvent;
import com.minh.common.events.ProductReservedEvent;
import com.minh.common.message.MessageCommon;
import com.minh.product_service.dto.ProductVariantDTO;
import com.minh.product_service.entity.ProductVariant;
import com.minh.product_service.entity.ReserveProduct;
import com.minh.product_service.enums.ReserveProductStatus;
import com.minh.product_service.repository.ReserveProductRepository;
import com.minh.product_service.service.ProductVariantService;
import com.minh.product_service.service.ReserveProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ReserveProductServiceImpl implements ReserveProductService {
    private final ReserveProductRepository reserveProductRepository;
    private final ProductVariantService productVariantService;
    private final MessageCommon messageCommon;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
    @Override
    public void reserveProduct(ProductReservedEvent event) {
        if (CollectionUtils.isEmpty(event.getReserveProductItems())) {
            return;
        }

        List<ReserveProduct> reserveProducts = new ArrayList<>();
        event.getReserveProductItems().forEach(item -> {
            ReserveProduct reserveProduct = new ReserveProduct();
            ProductVariantDTO variantDTO = productVariantService.findById(item.getProductVariantId());
            if (Objects.isNull(variantDTO)) {
                throw new RuntimeException(messageCommon.getMessage(ErrorCode.ProductVariant.NOT_FOUND, item.getProductVariantId()));
            }
            if (variantDTO.getQuantity() < item.getQuantity()) {
                throw new RuntimeException(messageCommon.getMessage(ErrorCode.ProductVariant.INSUFFICIENT_QUANTITY, item.getQuantity(), variantDTO.getQuantity()));
            }
            variantDTO.setQuantity(variantDTO.getQuantity() - item.getQuantity());
            productVariantService.updateProductVariant(variantDTO);
            reserveProduct.setId(UUID.randomUUID().toString());
            reserveProduct.setOrderId(event.getOrderId());
            reserveProduct.setProductVariantId(item.getProductVariantId());
            reserveProduct.setQuantity(item.getQuantity());
            reserveProduct.setStatus(ReserveProductStatus.locking);
            reserveProducts.add(reserveProduct);
        });
        reserveProductRepository.saveAll(reserveProducts);
    }

    /// Hàm thực hiện rollback đặt chỗ sản phẩm.
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
    public void rollbackReserveProduct(ProductReservedRollbackedEvent event) {
        String orderId = event.getOrderId();
        List<ReserveProduct> reserveProducts = reserveProductRepository.findAllByOrderId(orderId);
        if (reserveProducts.isEmpty()) {
            throw new RuntimeException("Không có sản phẩm nào cho đơn hàng này: " + orderId);
        }

        reserveProducts.forEach(reserveProduct -> {
            /// Trả lại số lượng sản phẩm vào kho.
            ProductVariantDTO variant = productVariantService.findById(reserveProduct.getProductVariantId());
            variant.setQuantity(variant.getQuantity() + reserveProduct.getQuantity());
            productVariantService.updateProductVariant(variant);
            /// Xoá bản ghi đặt chỗ sản phẩm.
            reserveProduct.setStatus(ReserveProductStatus.failed);
            reserveProductRepository.save(reserveProduct);
        });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
    public void confirmReservedProduct(ReserveProductConfirmedEvent event) {
        String orderId = event.getOrderId();
        List<ReserveProduct> reserveProducts = reserveProductRepository.findAllByOrderId(orderId);
        if (reserveProducts.isEmpty()) {
            throw new RuntimeException("Không có sản phẩm nào cho đơn hàng này: " + orderId);
        }
        /// Update status of all reserve products to 'completed'
        reserveProductRepository.updateAllItemsStatusToCompleted(orderId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
    public void rollbackReservedProduct(ProductReservedRollbackedEvent event) {
        String orderId = event.getOrderId();
        List<ReserveProduct> reserveProducts = reserveProductRepository.findAllByOrderId(orderId);
        if (reserveProducts.isEmpty()) {
            throw new RuntimeException("Không có sản phẩm nào cho đơn hàng này: " + orderId);
        }
        reserveProducts.forEach(reserveProduct -> {
            /// Trả lại số lượng sản phẩm vào kho.
            ProductVariantDTO variant = productVariantService.findById(reserveProduct.getProductVariantId());
            variant.setQuantity(variant.getQuantity() + reserveProduct.getQuantity());
            productVariantService.updateProductVariant(variant);
            /// Xoá bản ghi đặt chỗ sản phẩm.
            reserveProduct.setStatus(ReserveProductStatus.failed);
            reserveProductRepository.save(reserveProduct);
        });
    }
}
