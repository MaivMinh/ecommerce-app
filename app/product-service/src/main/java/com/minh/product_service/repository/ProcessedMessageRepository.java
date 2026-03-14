package com.minh.product_service.repository;

import com.minh.product_service.entity.ProcessedMessage;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, Field.Str> {
    Optional<ProcessedMessage> findById(String messageId);
}
