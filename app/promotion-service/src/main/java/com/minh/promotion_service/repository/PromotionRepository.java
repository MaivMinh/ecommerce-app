package com.minh.promotion_service.repository;

import com.minh.promotion_service.entity.Promotion;
import com.minh.promotion_service.query.queries.SearchPromotionsQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, String> {
    @Query(value = "select * from promotions P" +
            " where (P.code LIKE CONCAT('%', :#{#query.code}, '%')) " +
            " AND (P.status LIKE CONCAT('%', :#{#query.status}, '%')) "
            , nativeQuery = true)
    Page<Promotion> searchPromotions(@Param("query") SearchPromotionsQuery query, Pageable pageable);
}
