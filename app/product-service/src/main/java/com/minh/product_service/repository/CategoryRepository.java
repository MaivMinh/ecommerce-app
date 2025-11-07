package com.minh.product_service.repository;

import com.minh.product_service.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    /**
     * Finds a category by its slug.
     *
     * @param slug the slug of the category
     * @return the category with the specified slug, or null if not found
     */
    @Query(value = "SELECT c FROM Category c WHERE c.slug = ?1")
    Category findBySlug(String slug);

    List<Category> findByNameContainingIgnoreCase(String name);

}
