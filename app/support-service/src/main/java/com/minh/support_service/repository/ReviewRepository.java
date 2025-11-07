package com.minh.support_service.repository;

import com.minh.support_service.entity.Review;
import com.minh.support_service.payload.request.SearchReviewsRequest;
import com.minh.support_service.repository.projection.ReviewProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    @Query(value = "select r.id as id, " +
            "r.productId as productId, " +
            "r.username as username, " +
            "r.rating as rating, " +
            "r.content as content, " +
            "r.likes as likes, " +
            "ri.imageUrl as imageUrl " +
            "from Review r left join ReviewImage ri on r.id = ri.id " +
            "where (:#{#request.productId} is null or r.productId = :#{#request.productId}) " +
            "and (:#{#request.username} is null or r.username = :#{#request.username}) " +
            "and (:#{#request.minRating} is null or r.rating >= :#{#request.minRating}) " +
            "and (:#{#request.maxRating} is null or r.rating <= :#{#request.maxRating})"
    )
    Page<ReviewProjection> searchReviews(@Param("request") SearchReviewsRequest request,
                                         Pageable pageable);
}
