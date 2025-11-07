package com.minh.support_service.repository;

import com.minh.support_service.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
    @Query(value = "SELECT * FROM addresses WHERE username = :username", nativeQuery = true)
    List<Address> getAddressByUsername(@Param("username") String username);

    @Modifying
    @Query(value = "UPDATE addresses SET is_default = false WHERE username = :username", nativeQuery = true)
    void updateAllAddressToNonDefault(@Param("username") String username);
}
