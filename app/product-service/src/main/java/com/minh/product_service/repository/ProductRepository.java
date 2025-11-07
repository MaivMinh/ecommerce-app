package com.minh.product_service.repository;

import com.minh.product_service.entity.Product;
import com.minh.product_service.query.queries.SearchProductQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    @Query(value = "SELECT P FROM Product P WHERE P.id = :id")
    Optional<Product> findById(@Param("id") String id);

    @Query(value = "SELECT P FROM Product P WHERE P.slug = :slug")
    Product findBySlug(@Param("slug") String slug);

    @Query(value="SELECT p FROM Product p WHERE (:#{#query.isBestseller} IS NULL OR p.isBestseller = :#{#query.isBestseller}) AND (:#{#query.isNew} IS NULL OR p.isNew = :#{#query.isNew}) AND (:#{#query.isFeatured} IS NULL OR p.isFeatured = :#{#query.isFeatured}) AND (:#{#query.minPrice} IS NULL OR p.price >= :#{#query.minPrice}) AND (:#{#query.maxPrice} IS NULL OR p.price <= :#{#query.maxPrice}) AND (:#{#query.rating} IS NULL OR p.rating >= :#{#query.rating}) AND (:#{#query.categoryIds.size()} = 0 OR p.categoryId IN :#{#query.categoryIds}) AND (:#{#query.status} IS NULL OR p.status = :#{#query.status})")
    Page<Product> searchProducts(@Param("query") SearchProductQuery query, Pageable pageable);

    @Query(value = "SELECT p FROM Product p ORDER BY p.createdAt DESC")
    List<Product> findNewestProducts(Pageable pageable);

    @Query(value = "select p from Product p left join Category c on p.categoryId = c.id where lower(p.name) like lower(concat('%', :keyword,'%')) or lower(p.description) like lower(concat('%', :keyword,'%')) or lower(c.name) like lower(concat('%', :keyword,'%')) or lower(c.description) like lower(concat('%', :keyword, '%')) ")
    Page<Product> searchProductByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
