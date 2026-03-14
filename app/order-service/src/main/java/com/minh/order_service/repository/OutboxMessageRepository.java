package com.minh.order_service.repository;

import com.minh.order_service.entity.OutboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxMessageRepository extends JpaRepository<OutboxMessage, String> {

    @Query(value = """
            select om from OutboxMessage om where om.processed = false   
            """)
    List<OutboxMessage> findAllByProcessed();
}
