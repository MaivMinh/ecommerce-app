package com.minh.order_service.repository;

import com.minh.order_service.entity.Promotion;
import com.minh.order_service.payload.request.SearchPromotionsRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, String> {
    @Query(value = "select * from promotions P" +
            " where (P.code LIKE CONCAT('%', :#{#query.code}, '%')) " +
            " AND (P.status LIKE CONCAT('%', :#{#query.status}, '%')) "
            , nativeQuery = true)
    Page<Promotion> searchPromotions(@Param("query") SearchPromotionsRequest query, Pageable pageable);

    @Query(value = """
                select * from promotions p where p.end_date >= CURRENT_DATE() and p.start_date <= CURRENT_DATE() and p.status = 'active'
            """, nativeQuery = true)
    List<Promotion> findAllActivePromotions();
}
