package com.minh.product_service.repository;

import com.minh.product_service.entity.OutboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxMessageRepository extends JpaRepository<OutboxMessage, String> {

    @Query(value = """
            SELECT *\s
            FROM outbox_messages om\s
            WHERE om.processed = FALSE\s
            ORDER BY created_at\s
            LIMIT :N
            FOR UPDATE SKIP LOCKED
           \s""", nativeQuery = true)
    List<OutboxMessage> findTopNUnprocessedMessage(@Param(value = "N") int N);

    boolean existsByMessageId(String messageId);
}