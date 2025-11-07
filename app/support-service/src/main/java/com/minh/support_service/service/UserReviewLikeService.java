package com.minh.support_service.service;

import com.minh.support_service.DTO.UserReviewLikeDto;

public interface UserReviewLikeService {
    UserReviewLikeDto findByReviewIdAndUsername(String reviewId, String username);

    void likeReviewWithUser(UserReviewLikeDto entity);

    void dislikeReviewWithUserById(String id);
}
