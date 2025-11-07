package com.minh.support_service.controller;

import com.minh.common.constants.ResponseMessages;
import com.minh.common.response.ResponseData;
import com.minh.support_service.DTO.ReviewDto;
import com.minh.support_service.payload.request.SearchReviewsRequest;
import com.minh.support_service.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@Validated
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData> createReview(@RequestBody @Valid ReviewDto request) {
        reviewService.createReview(request);
        return ResponseEntity.ok(new ResponseData(200, ResponseMessages.SUCCESS, null));
    }

    @PostMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseData> searchReviewsRequest(@RequestBody SearchReviewsRequest request) {
        ResponseData response = reviewService.searchReviewsRequest(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PatchMapping("/{reviewId}/like")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData> likeReview(@PathVariable(name = "reviewId") String reviewId) {
        ResponseData response = reviewService.likeReview(reviewId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PatchMapping("/{reviewId}/dislike")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseData> dislikeReview(@PathVariable(name = "reviewId") String reviewId) {
        ResponseData response = reviewService.dislikeReview(reviewId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
