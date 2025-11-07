package com.minh.support_service.service.impl;

import com.minh.support_service.DTO.PurchasedProductDto;
import com.minh.support_service.entity.PurchasedProduct;
import com.minh.support_service.repository.PurchasedProductRepository;
import com.minh.support_service.service.PurchasedProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PurchasedProductServiceImpl implements PurchasedProductService {
    private final PurchasedProductRepository repository;
    private final ModelMapper modelMapper;

    @Override
    public void savePurchasedOrder(PurchasedProductDto source) {
        if (Objects.isNull(source)) {
            throw new RuntimeException("Đơn hàng đã mua không hợp lệ");
        }

        PurchasedProduct saved = repository.findPurchasedProductByProductIdAndUsername(
                source.getProductId(),
                source.getUsername()
        );
        if (Objects.nonNull(saved)) {
            // Đã tồn tại, không cần lưu lại
            return;
        }

        PurchasedProduct entity = new PurchasedProduct();
        modelMapper.map(source, entity);
        repository.save(entity);
    }

    @Override
    public PurchasedProductDto findPurchasedProductByProductIdAndUsername(String productId, String username) {
        PurchasedProduct entity = repository.findPurchasedProductByProductIdAndUsername(productId, username);
        if (Objects.isNull(entity)) {
            return null;
        }
        return modelMapper.map(entity, PurchasedProductDto.class);
    }
}
