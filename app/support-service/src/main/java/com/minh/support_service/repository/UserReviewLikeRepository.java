package com.minh.support_service.repository;

import com.minh.support_service.entity.UserReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReviewLikeRepository extends JpaRepository<UserReviewLike, String> {
    @Query(value = "SELECT * FROM user_review_likes url WHERE url.review_id = :reviewId AND url.username = :username", nativeQuery = true)
    UserReviewLike findByReviewIdAndUsername(@Param("reviewId") String reviewId,@Param("username") String username);
}
