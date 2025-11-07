package com.minh.product_service.service.impl;

import com.minh.product_service.dto.ProductImageDTO;
import com.minh.product_service.entity.ProductImage;
import com.minh.product_service.repository.ProductImageRepository;
import com.minh.product_service.repository.ProductRepository;
import com.minh.product_service.service.ProductImageService;
import com.netflix.spectator.api.Registry;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductImageRepository productImageRepository;
    private final ModelMapper modelMapper;

    @Override
    public void createProductImage(ProductImageDTO productImageDTO) {
        ProductImage productImage = modelMapper.map(productImageDTO, ProductImage.class);
        productImage.setId(UUID.randomUUID().toString());
        productImageRepository.save(productImage);
    }

    @Override
    public void updateProductImage(ProductImageDTO productImageDTO) {
        ProductImage productImage
                = productImageRepository.findById(productImageDTO.getId()).orElse(null);
        if (productImage == null) {
            throw new IllegalArgumentException("Product image not found with id: " + productImageDTO.getId());
        }

        modelMapper.map(productImageDTO, productImage);
        productImageRepository.save(productImage);
    }

    @Override
    public void deleteProductImage(String productImageId) {
        if (!productImageRepository.existsById(productImageId)) {
            throw new IllegalArgumentException("Product image not found with id: " + productImageId);
        }
        productImageRepository.deleteById(productImageId);
    }

    @Override
    public List<ProductImageDTO> findProductImagesByProductId(String id) {
        List<ProductImage> productImages = productImageRepository.findAllByProductId(id);
        if (productImages.isEmpty()) {
            return List.of();
        }
        return productImages.stream()
                .map(image -> modelMapper.map(image, ProductImageDTO.class))
                .toList();
    }

    /**
     * Hàm thực hiện tìm kiếm tất cả ảnh tương ứng với danh sách productId
     *
     * @param productIds
     * @return Danh sách ảnh tương ứng với danh sách productId
     */
    @Override
    public List<ProductImageDTO> findProductImagesByProductIds(List<String> productIds) {
        List<ProductImage> productImages = productImageRepository.findProductImagesByProductIds(productIds);
        if (productImages.isEmpty()) {
            return List.of();
        }
        return productImages.stream()
                .map(image -> modelMapper.map(image, ProductImageDTO.class))
                .toList();
    }
}
