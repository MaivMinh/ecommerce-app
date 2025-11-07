package com.minh.notify_service.repository;

import com.minh.notify_service.entity.NotificationSendLog;
import com.minh.notify_service.payload.request.SearchSendLogsRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationSendLogRepository extends JpaRepository<NotificationSendLog, String> {
    @Query(value = "select nsl from NotificationSendLog nsl " +
            "where (:#{#request.templateCode} is null or nsl.templateCode = :#{#request.templateCode}) " +
            "and (:#{#request.recipient} is null or nsl.recipient like %:#{#request.recipient}%) " +
            "and (:#{#request.status} is null or nsl.status = :#{#request.status}) " +
            "and (:#{#request.startDate} is null or nsl.createdAt >= :#{#request.startDate}) " +
            "and (:#{#request.endDate} is null or nsl.createdAt <= :#{#request.endDate}) ")
    Page<NotificationSendLog> searchSendLogs(@Param("request") SearchSendLogsRequest request, Pageable pageable);
}