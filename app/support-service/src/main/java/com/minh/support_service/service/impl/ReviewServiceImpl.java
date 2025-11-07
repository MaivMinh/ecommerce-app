package com.minh.support_service.service.impl;

import com.minh.common.constants.ErrorCode;
import com.minh.common.constants.ResponseMessages;
import com.minh.common.message.MessageCommon;
import com.minh.common.response.ResponseData;
import com.minh.common.utils.AppUtils;
import com.minh.support_service.DTO.PurchasedProductDto;
import com.minh.support_service.DTO.ReviewDto;
import com.minh.support_service.DTO.UserReviewLikeDto;
import com.minh.support_service.entity.Review;
import com.minh.support_service.entity.ReviewImage;
import com.minh.support_service.payload.request.SearchReviewsRequest;
import com.minh.support_service.repository.ReviewImageRepository;
import com.minh.support_service.repository.ReviewRepository;
import com.minh.support_service.repository.projection.ReviewProjection;
import com.minh.support_service.service.PurchasedProductService;
import com.minh.support_service.service.ReviewService;
import com.minh.support_service.service.UserReviewLikeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ModelMapper modelMapper;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final MessageCommon messageCommon;
    private final UserReviewLikeService userReviewLikeService;
    private final PurchasedProductService purchasedOrderService;

    @Override
    public void createReview(ReviewDto source) {
        /// Kiểm tra xem người dùng đã mua sản phẩm chưa.
        PurchasedProductDto purchasedOrderDto = purchasedOrderService.findPurchasedProductByProductIdAndUsername(
                source.getProductId(),
                AppUtils.getUsername()
        );
        if (Objects.isNull(purchasedOrderDto)) {
            throw new RuntimeException("Bạn chưa mua sản phẩm này, không thể đánh giá.");
        }

        Review dest = new Review();
        modelMapper.map(source,dest);
        dest.setId(AppUtils.generateUUIDv7());
        dest.setLikes(0);
        dest.setUsername(AppUtils.getUsername());
        Review saved = reviewRepository.save(dest);

        if (StringUtils.hasText(source.getImageUrl())) {
            ReviewImage reviewImage = new ReviewImage();
            reviewImage.setId(AppUtils.generateUUIDv7());
            reviewImage.setImageUrl(source.getImageUrl());
            reviewImage.setReviewId(saved.getId());
            reviewImageRepository.save(reviewImage);
        }
    }

    @Override
    public ResponseData searchReviewsRequest(SearchReviewsRequest request) {
        if (Objects.isNull(request)) {
            return new ResponseData(400, "Yêu cầu không hợp lệ", null);
        }

        Pageable pageable = AppUtils.toPageable(request);
        Page<ReviewProjection> reviewProjections = reviewRepository.searchReviews(request, pageable);
        Page<ReviewDto> reviews = reviewProjections.map(projection -> {
            ReviewDto dto = new ReviewDto();
            dto.setId(projection.getId());
            dto.setProductId(projection.getProductId());
            dto.setUsername(projection.getUsername());
            dto.setRating(projection.getRating());
            dto.setContent(projection.getContent());
            dto.setLikes(projection.getLikes());
            dto.setImageUrl(projection.getImageUrl());
            return dto;
        });

        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(reviews)
                .build();
    }

    @Override
    @Transactional
    public ResponseData likeReview(String reviewId) {
        if (!StringUtils.hasText(reviewId)) {
            return new ResponseData(400, "Review ID không được để trống", null);
        }

        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (Objects.isNull(review)) {
            return new ResponseData(404, messageCommon.getMessage(ErrorCode.Review.NOT_FOUND, reviewId), null);
        }
        String username = AppUtils.getUsername();
        UserReviewLikeDto userReviewLikeDto = userReviewLikeService.findByReviewIdAndUsername(reviewId, username);
        if (Objects.nonNull(userReviewLikeDto)) {
            return new ResponseData(400, "Người dùng đã thích đánh giá này", null);
        }

        review.setLikes(review.getLikes() + 1);
        reviewRepository.save(review);
        UserReviewLikeDto entity = new UserReviewLikeDto();
        entity.setId(AppUtils.generateUUIDv7());
        entity.setReviewId(reviewId);
        entity.setUsername(username);
        userReviewLikeService.likeReviewWithUser(entity);
        return new ResponseData(200, ResponseMessages.SUCCESS, null);
    }

    @Override
    public ResponseData dislikeReview(String reviewId) {
        if (!StringUtils.hasText(reviewId)) {
            return new ResponseData(400, "Review ID không được để trống", null);
        }

        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (Objects.isNull(review)) {
            return new ResponseData(404, messageCommon.getMessage(ErrorCode.Review.NOT_FOUND, reviewId), null);
        }
        String username = AppUtils.getUsername();
        UserReviewLikeDto userReviewLikeDto = userReviewLikeService.findByReviewIdAndUsername(reviewId, username);
        if (Objects.isNull(userReviewLikeDto)) {
            return new ResponseData(400, "Người dùng chưa thích đánh giá này", null);
        }

        review.setLikes(review.getLikes() - 1);
        reviewRepository.save(review);
        userReviewLikeService.dislikeReviewWithUserById(userReviewLikeDto.getId());
        return new ResponseData(200, ResponseMessages.SUCCESS, null);
    }
}
