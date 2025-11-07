package com.minh.support_service.repository.projection;

public interface ReviewProjection {
    String getId();
    String getProductId();
    String getUsername();
    Integer getRating();
    String getContent();
    Integer getLikes();
    String getImageUrl();
}
