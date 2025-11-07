package com.minh.order_service.query.repository;

import com.minh.order_service.query.controller.SearchOrdersRequest;
import com.minh.order_service.query.entity.Order;
import com.minh.order_service.query.queries.SearchOrdersForUserQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    @Query(value = "SELECT od.id " +
            "FROM orders od " +
            "WHERE (COALESCE(:#{#request.keyword}, NULL) IS NULL OR od.id LIKE CONCAT('%', :#{#request.keyword}, '%')) " +
            "AND (COALESCE(:#{#request.keyword}, NULL) IS NULL OR od.created_by LIKE CONCAT('%', :#{#request.keyword}, '%')) " +
            "AND (COALESCE(:#{#request.status}, NULL) IS NULL OR od.status = :#{#request.status}) " +
            "ORDER BY od.id DESC " +
            "LIMIT :#{#pageable.pageSize} OFFSET :#{#pageable.offset}",
            countQuery = "SELECT COUNT(*) FROM orders od " +
                    "WHERE (COALESCE(:#{#request.keyword}, NULL) IS NULL OR od.id LIKE CONCAT('%', :#{#request.keyword}, '%')) " +
                    "AND (COALESCE(:#{#request.keyword}, NULL) IS NULL OR od.created_by LIKE CONCAT('%', :#{#request.keyword}, '%')) " +
                    "AND (COALESCE(:#{#request.status}, NULL) IS NULL OR od.status = :#{#request.status})",
            nativeQuery = true)
    Page<String> searchOrderIds(@Param("request") SearchOrdersRequest request, Pageable pageable);

    @Query(value = "select o.id from Order o " +
            " where (COALESCE(:#{#query.keyword}, null) is null or o.id like concat('%', :#{#query.keyword}, '%') " +
            " or o.createdBy like concat('%', :#{#query.keyword}, '%'))" +
            " and (COALESCE(:#{#query.status}, null) is null or o.status = :#{#query.status})" +
            " and (COALESCE(:#{#query.createdUser}, null) is null or o.createdBy = :#{#query.createdUser})" +
            " order by o.id desc")
    Page<String> searchOrderIdsForUser(@Param("query") SearchOrdersForUserQuery query, Pageable pageable);
}
