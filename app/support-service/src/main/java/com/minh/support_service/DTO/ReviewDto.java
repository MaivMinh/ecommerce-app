package com.minh.support_service.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDto {
    private String id;
    private String productId;
    private String username;
    private Integer rating;
    private String content;
    private Integer likes;
    private String imageUrl;
}
