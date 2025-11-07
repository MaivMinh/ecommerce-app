package com.minh.support_service.payload.request;

import com.minh.common.DTOs.SearchDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchReviewsRequest extends SearchDTO {
    private String productId;
    private String username;
    private Integer minRating;
    private Integer maxRating;
}
