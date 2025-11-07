package com.minh.support_service.service;

import com.minh.common.response.ResponseData;
import com.minh.support_service.DTO.ReviewDto;
import com.minh.support_service.payload.request.SearchReviewsRequest;

public interface ReviewService {
    void createReview(ReviewDto request);

    ResponseData searchReviewsRequest(SearchReviewsRequest request);

    ResponseData likeReview(String reviewId);

    ResponseData dislikeReview(String reviewId);
}
