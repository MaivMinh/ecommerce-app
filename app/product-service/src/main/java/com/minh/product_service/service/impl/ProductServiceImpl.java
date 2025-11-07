package com.minh.product_service.service.impl;

import com.minh.common.constants.ErrorCode;
import com.minh.common.constants.ResponseMessages;
import com.minh.common.enums.ProductStatus;
import com.minh.common.message.MessageCommon;
import com.minh.common.response.ResponseData;
import com.minh.common.utils.AppUtils;
import com.minh.product_service.command.events.ProductCreatedEvent;
import com.minh.product_service.command.events.ProductDeletedEvent;
import com.minh.product_service.command.events.ProductUpdatedEvent;
import com.minh.product_service.dto.ProductDTO;
import com.minh.product_service.dto.ProductImageDTO;
import com.minh.product_service.dto.ProductSearchDTO;
import com.minh.product_service.dto.ProductVariantDTO;
import com.minh.product_service.entity.Product;
import com.minh.product_service.payload.response.ProductVariantGrpc;
import com.minh.product_service.query.queries.*;
import com.minh.product_service.repository.ProductRepository;
import com.minh.product_service.repository.ReserveProductRepository;
import com.minh.product_service.service.ProductImageService;
import com.minh.product_service.service.ProductService;
import com.minh.product_service.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import product_service.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final MessageCommon messageCommon;
    private final ProductVariantService productVariantService;
    private final ProductImageService productImageService;
    private final ModelMapper modelMapper;
    private final ReserveProductRepository reserveProductRepository;

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
    public ResponseData findProducts(FindProductsQuery query) {
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());
        Page<Product> page = productRepository.findAll(pageable);
        Map<String, ProductDTO> productDTOS = page
                .stream()
                .map(product -> {
                    ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
                    productDTO.setProductVariants(new ArrayList<>());   /// null-check.
                    productDTO.setImages(new ArrayList<>());
                    return productDTO;
                })
                .collect(Collectors.toMap(ProductDTO::getId, dto -> dto));


        List<String> productIds = productDTOS.keySet().stream().toList();
        List<ProductVariantDTO> productVariantDTOS = productVariantService.findProductVariantsByProductIds(productIds);

        /// Fill product variants into each productDTO.
        for (ProductVariantDTO productVariantDTO : productVariantDTOS) {
            ProductDTO productDTO = productDTOS.get(productVariantDTO.getProductId());
            if (!Objects.isNull(productDTO)) {
                /// Add product variant to the productDTO.
                productDTO.getProductVariants().add(productVariantDTO);
            }
        }

        List<ProductImageDTO> productImageDTOS = productImageService.findProductImagesByProductIds(productIds);
        /// Fill product images into each productDTO.
        for (ProductImageDTO productImageDTO : productImageDTOS) {
            ProductDTO productDTO = productDTOS.get(productImageDTO.getProductId());
            if (!Objects.isNull(productDTO)) {
                /// Add product image to the productDTO.
                productDTO.getImages().add(productImageDTO.getImageUrl());
            }
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("page", page.getNumber() + 1);
        payload.put("size", page.getSize());
        payload.put("totalElements", page.getTotalElements());
        payload.put("totalPages", page.getTotalPages());
        payload.put("products", productDTOS.values().stream().collect(Collectors.toList()));

        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message(ResponseMessages.SUCCESS)
                .data(payload)
                .build();
    }

    @Override
    public ResponseData findProductById(FindProductByIdQuery query) {
        Product product = productRepository.findById(query.getProductId()).orElse(null);
        if (product == null) {
            return ResponseData.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message(messageCommon.getMessage(ErrorCode.Product.NOT_FOUND, query.getProductId()))
                    .build();
        }

        ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message(ResponseMessages.SUCCESS)
                .data(productDTO)
                .build();
    }

    @Override
    public ResponseData findProductBySlug(FindProductBySlugQuery query) {
        Product product = productRepository.findBySlug(query.getSlug());
        if (product == null) {
            return ResponseData.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message(messageCommon.getMessage(ErrorCode.Product.SLUG_NOT_FOUND, query.getSlug()))
                    .build();
        }

        ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);

        List<ProductImageDTO> productImageDTOs = productImageService.findProductImagesByProductId(product.getId());
        List<ProductVariantDTO> productVariantDTOs = productVariantService.findProductVariantsByProductId(product.getId());

        productDTO.setProductVariants(new ArrayList<>(productVariantDTOs));
        productDTO.setImages(productImageDTOs.stream().map(ProductImageDTO::getImageUrl).collect(Collectors.toList()));

        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message(ResponseMessages.SUCCESS)
                .data(productDTO)
                .build();
    }

    @Override
    public void createProduct(ProductCreatedEvent event) {
        Product product = modelMapper.map(event, Product.class);
        product.setSlug(generateSlug(product.getName()));
        product.setStatus(ProductStatus.valueOf(event.getStatus()));
        productRepository.save(product);

        /// Tạo mới các biến thể liên quan đến sản phẩm.
        if (!CollectionUtils.isEmpty(event.getProductVariants())) {
            event.getProductVariants().forEach(productVariantDTO -> {
                productVariantDTO.setProductId(product.getId());
                productVariantService.createProductVariant(productVariantDTO);
            });
        }

        /// Lưu danh sách hình ảnh.
        if (!CollectionUtils.isEmpty(event.getImages())) {
            event.getImages().forEach(image -> {
                ProductImageDTO productImageDTO = new ProductImageDTO();
                productImageDTO.setId(UUID.randomUUID().toString());
                productImageDTO.setProductId(product.getId());
                productImageDTO.setImageUrl(image);
                productImageService.createProductImage(productImageDTO);
            });
        }
    }

    @Override
    public void updateProduct(ProductUpdatedEvent event) {
        Product product = productRepository.findById(event.getId()).orElse(null);
        if (product == null) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.Product.NOT_FOUND, event.getId()));
        }
        modelMapper.map(event, product);
        productRepository.save(product);

        List<ProductVariantDTO> productVariantDTOS = event.getProductVariants();
        Map<String, ProductVariantDTO> variantMap = productVariantDTOS.stream()
                .collect(Collectors.toMap(ProductVariantDTO::getId, variant -> variant));

        /// Nếu một biến thể không còn trong danh sách mới, xoá nó.
        List<ProductVariantDTO> existingVariants = productVariantService.findProductVariantsByProductId(product.getId());
        existingVariants.forEach(productVariantDTO -> {
            if (!variantMap.containsKey(productVariantDTO.getId())) {
                productVariantService.deleteProductVariant(productVariantDTO.getId());
            } else {
                // Cập nhật biến thể nếu tồn tại trong danh sách mới.
                ProductVariantDTO updatedVariant = variantMap.get(productVariantDTO.getId());
                updatedVariant.setProductId(product.getId());
                productVariantService.updateProductVariant(updatedVariant);
            }
        });

        /// Duyệt danh sách biến thể mới và tạo mới nếu chưa tồn tại.
        for (ProductVariantDTO newVariant : productVariantDTOS) {
            if (newVariant.getId() == null || newVariant.getId().isEmpty()) {
                newVariant.setProductId(product.getId());
                productVariantService.createProductVariant(newVariant);
            }
        }

        // Xoá tất cả hình ảnh cũ liên quan đến sản phẩm.
        List<ProductImageDTO> existingImages = productImageService.findProductImagesByProductId(product.getId());
        existingImages.forEach(image -> productImageService.deleteProductImage(image.getId()));

        if (!CollectionUtils.isEmpty(event.getImages())) {
            // Lưu lại các hình ảnh mới.
            event.getImages().forEach(image -> {
                ProductImageDTO productImageDTO = new ProductImageDTO();
                productImageDTO.setId(UUID.randomUUID().toString());
                productImageDTO.setProductId(product.getId());
                productImageDTO.setImageUrl(image);
                productImageService.createProductImage(productImageDTO);
            });
        }
    }

    @Override
    public void deleteProduct(ProductDeletedEvent event) {
        Product product = productRepository.findById(event.getId()).orElse(null);
        if (product == null) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.Product.NOT_FOUND, event.getId()));
        }
        productRepository.delete(product);
    }

    @Override
    public ResponseData searchProducts(SearchProductQuery query) {
        Pageable pageable = null;
        if (StringUtils.hasText(query.getSort())) {
            List<Sort.Order> orders = new ArrayList<>();
            String[] sortParams = query.getSort().split(",");
            for (String sortParam : sortParams) {
                orders.add(new Sort.Order(Sort.Direction.fromString(sortParam.split(":")[1].toUpperCase()), sortParam.split(":")[0]));
            }
            pageable = PageRequest.of(query.getPage(), query.getSize(), Sort.by(orders));
        } else pageable = PageRequest.of(query.getPage(), query.getSize());
        Page<Product> products = productRepository.searchProducts(query, pageable);

        List<String> productIds = products.stream()
                .map(Product::getId)
                .toList();
        List<ProductVariantDTO> productVariants = productVariantService.findProductVariantsByProductIds(productIds);
        List<ProductImageDTO> productImages = productImageService.findProductImagesByProductIds(productIds);
        Map<String, ProductDTO> productDTOS = products.stream()
                .map(product -> {
                    ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
                    productDTO.setProductVariants(new ArrayList<>());
                    productDTO.setImages(new ArrayList<>());
                    return productDTO;
                })
                .collect(Collectors.toMap(ProductDTO::getId, dto -> dto));

        for (ProductVariantDTO dto : productVariants) {
            ProductDTO productDTO = productDTOS.get(dto.getProductId());
            if (productDTO != null) {
                productDTO.getProductVariants().add(dto);
            }
        }
        for (ProductImageDTO dto : productImages) {
            ProductDTO productDTO = productDTOS.get(dto.getProductId());
            if (productDTO != null) {
                productDTO.getImages().add(dto.getImageUrl());
            }
        }
        List<ProductDTO> result = new ArrayList<>(productDTOS.values());

        Map<String, Object> data = new HashMap<>();
        data.put("products", result);
        data.put("page", products.getNumber() + 1);
        data.put("size", products.getSize());
        data.put("totalElements", products.getTotalElements());
        data.put("totalPages", products.getTotalPages());

        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message(ResponseMessages.SUCCESS)
                .data(data)
                .build();
    }

    @Override
    public ResponseData findProductVariantsByProductId(FindProductVariantsByProductIdQuery query) {
        Product product = productRepository.findById(query.getProductId()).orElse(null);
        if (product == null) {
            return ResponseData.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message(messageCommon.getMessage(ErrorCode.Product.SLUG_NOT_FOUND, query.getProductId()))
                    .build();
        }

        List<ProductVariantDTO> productVariants = productVariantService.findProductVariantsByProductId(product.getId());
        List<ProductVariantDTO> data = productVariants.stream()
                .map(variant -> {
                    ProductVariantDTO variantDTO = modelMapper.map(variant, ProductVariantDTO.class);
                    variantDTO.setProductId(product.getId());
                    return variantDTO;
                })
                .collect(Collectors.toList());

        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message(ResponseMessages.SUCCESS)
                .data(data)
                .build();
    }

    @Override
    public ResponseData findNewestProducts(FindNewestProductsQuery query) {
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());
        List<Product> products = productRepository.findNewestProducts(pageable);
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message(ResponseMessages.SUCCESS)
                .data(products.stream()
                        .map(product -> {
                            ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
                            productDTO.setProductVariants(new ArrayList<>());
                            productDTO.setImages(new ArrayList<>());
                            return productDTO;
                        })
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public FindProductVariantByIdResponse findProductVariantById(FindProductVariantByIdRequest request) {
        return null;
    }

    /**
     * Tìm nhiều biến thể sản phẩm theo danh sách ID.
     *
     * @param request
     * @return FindProductVariantsByIdsResponse
     */
    @Override
    public FindProductVariantsByIdsResponse findProductVariantsByIds(FindProductVariantsByIdsRequest request) {
        if (CollectionUtils.isEmpty(request.getCartItemFieldList())) {
            return FindProductVariantsByIdsResponse.newBuilder()
                    .setStatus(HttpStatus.BAD_REQUEST.value())
                    .setMessage("Không tìm thấy danh sách ID.")
                    .build();
        }

        List<CartItemField> cartItemFields = request.getCartItemFieldList().stream().toList();
        Map<String, Object> map = new HashMap<>();
        List<String> productVariantIds = new ArrayList<>();
        for (CartItemField cartItemField : cartItemFields) {
            map.put(cartItemField.getProductVariantId(), cartItemField);
            productVariantIds.add(cartItemField.getProductVariantId());
        }
        List<ProductVariant> productVariants = new ArrayList<>();
        List<ProductVariantDTO> productVariantDTOS = productVariantService.findProductVariantsByIds(productVariantIds);
        if (CollectionUtils.isEmpty(productVariantDTOS)) {
            return FindProductVariantsByIdsResponse.newBuilder()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("Không tìm thấy biến thể sản phẩm nào.")
                    .build();
        }
        Product product = productRepository.findById(productVariantDTOS.get(0).getProductId()).orElse(null);
        if (!CollectionUtils.isEmpty(productVariantDTOS)) {
            productVariantDTOS.forEach(productVariant -> {
                CartItemField cartItemField = (CartItemField) map.get(productVariant.getId());
                ProductVariant.Builder builder = ProductVariant.newBuilder();
                builder.setCartItemId(cartItemField.getCartItemId());
                builder.setId(productVariant.getId());
                builder.setSize(productVariant.getSize());
                builder.setOriginalPrice(productVariant.getOriginalPrice());
                builder.setPrice(productVariant.getPrice());
                builder.setColorName(productVariant.getColorName());
                builder.setColorHex(productVariant.getColorHex());
                builder.setCover(product.getCover());
                builder.setSlug(product.getSlug());
                builder.setName(product.getName());
                builder.setProductId(product.getId());
                ProductVariant variant = builder.build();
                productVariants.add(variant);
            });
        }

        return FindProductVariantsByIdsResponse.newBuilder()
                .setStatus(HttpStatus.OK.value())
                .setMessage(ResponseMessages.SUCCESS)
                .addAllProductVariant(productVariants)
                .build();
    }


    @Override
    public FindProductVariantByListProductVariantIdResponse findProductVariantByListId(FindProductVariantByListProductVariantIdRequest request) {
        Map<String, String> variantToOrderItemMapping = new HashMap<>();

        for (OrderItemAndProductVariantId item : request.getIdsList()) {
            variantToOrderItemMapping.put(item.getProductVariantId(), item.getOrderItemId());

        }

        List<String> productVariantIds = request.getIdsList().stream().map(
                OrderItemAndProductVariantId::getProductVariantId
        ).toList();

        List<ProductVariantGrpc> productVariantGrpc = productVariantService.findProductVariantsByIdsGrpc(productVariantIds);
        if (CollectionUtils.isEmpty(productVariantGrpc)) {
            return FindProductVariantByListProductVariantIdResponse.newBuilder()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("Không tìm thấy biến thể sản phẩm nào.")
                    .build();
        }
        List<ProductVariantRes> productVariantRes = productVariantGrpc.stream()
                .map(productVariant -> ProductVariantRes.newBuilder()
                        .setId(productVariant.getId())
                        .setName(productVariant.getName())
                        .setSlug(productVariant.getSlug())
                        .setSize(productVariant.getSize())
                        .setColorName(productVariant.getColorName())
                        .setColorHex(productVariant.getColorHex())
                        .setPrice(productVariant.getPrice())
                        .setCover(productVariant.getCover())
                        .setOriginalPrice(productVariant.getOriginalPrice())
                        .setOrderItemId(variantToOrderItemMapping.get(productVariant.getId()))
                        .build())
                .toList();
        return FindProductVariantByListProductVariantIdResponse.newBuilder()
                .setStatus(HttpStatus.OK.value())
                .setMessage(ResponseMessages.SUCCESS)
                .addAllProductVariants(productVariantRes)
                .build();
    }

    @Override
    public ResponseData searchProductByKeyword(SearchProductByKeywordQuery query) {
        Pageable pageable = AppUtils.toPageable(query);

        Page<Product> pagedProducts = productRepository.searchProductByKeyword(query.getKeyword(), pageable);
        Map<String, Object> data = new HashMap<>();
        List<ProductSearchDTO> productSearchDTOS = pagedProducts.stream()
                .map(product -> modelMapper.map(product, ProductSearchDTO.class))
                .collect(Collectors.toList());

        data.put("products", productSearchDTOS);
        data.put("page", pagedProducts.getNumber() + 1);
        data.put("size", pagedProducts.getSize());
        data.put("totalElements", pagedProducts.getTotalElements());
        data.put("totalPages", pagedProducts.getTotalPages());

        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message(ResponseMessages.SUCCESS)
                .data(data)
                .build();
    }

    @Override
    public ResponseData findProductByProductVariantId(FindProductByProductVariantIdQuery query) {
        if (Objects.isNull(query) || !StringUtils.hasText(query.getProductVariantId())) {
            return ResponseData.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Dữ liệu không hợp lệ.")
                    .build();
        }

        ProductVariantDTO productVariantDTO = productVariantService.findById(query.getProductVariantId());
        if (Objects.isNull(productVariantDTO)) {
            return ResponseData.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message(messageCommon.getMessage(ErrorCode.ProductVariant.NOT_FOUND, query.getProductVariantId()))
                    .build();
        }

        String productId = productVariantDTO.getProductId();
        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(productId)
                .build();
    }

    @Override
    public FindProductInfoByProductVariantIdResponse findProductInfoByProductVariantId(FindProductInfoByProductVariantIdRequest request) {
        if (CollectionUtils.isEmpty(request.getProductVariantIdList())) {
            return FindProductInfoByProductVariantIdResponse.newBuilder()
                    .setStatus(HttpStatus.BAD_REQUEST.value())
                    .setMessage("Danh sách productVariantId trống.")
                    .build();
        }

        List<String> productVariantIds = request.getProductVariantIdList().stream().toList();
        List<ProductVariantDTO> productVariantDTOS = productVariantService.findProductVariantsByIds(productVariantIds);
        if (CollectionUtils.isEmpty(productVariantDTOS)) {
            return FindProductInfoByProductVariantIdResponse.newBuilder()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("Không tìm thấy biến thể sản phẩm nào.")
                    .build();
        }

        List<ProductInfo> productInfos = new ArrayList<>();
        for (ProductVariantDTO dto : productVariantDTOS) {
            Product product = productRepository.findById(dto.getProductId()).orElse(null);
            if (product != null) {
                ProductInfo.Builder builder = ProductInfo.newBuilder();
                builder.setProductVariantId(dto.getId());
                builder.setProductId(product.getId());
                builder.setProductName(product.getName());
                builder.setColorName(dto.getColorName());
                builder.setSize(dto.getSize());
                builder.setCover(product.getCover());
                ProductInfo productInfo = builder.build();
                productInfos.add(productInfo);
            }
        }

        log.info("Số lượng productInfos tìm thấy: {}", productInfos.size());

        return FindProductInfoByProductVariantIdResponse.newBuilder()
                .setStatus(HttpStatus.OK.value())
                .setMessage(ResponseMessages.SUCCESS)
                .addAllProducts(productInfos)
                .build();

    }
}
