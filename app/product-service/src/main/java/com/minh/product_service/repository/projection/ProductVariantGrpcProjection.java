package com.minh.product_service.repository.projection;

public interface ProductVariantGrpcProjection {
    String getId();
    String getName();
    String getSlug();
    String getSize();
    String getColorName();
    String getColorHex();
    String getCover();
    Double getPrice();
    Double getOriginalPrice();
}
