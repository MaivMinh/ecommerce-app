package com.minh.event_service.repository;

import com.minh.event_service.entity.PlayerVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerVoucherRepository extends JpaRepository<PlayerVoucher, String> {

}
