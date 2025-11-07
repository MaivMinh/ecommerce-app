package com.minh.support_service.repository;

import com.minh.support_service.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {

    @Query(value = "select * from carts where username = :username", nativeQuery = true)
    Cart findByUsername(@Param(value = "username") String username);
}
