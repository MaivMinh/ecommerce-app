package com.minh.notify_service.repository;

import com.minh.notify_service.entity.NotificationTemplate;
import com.minh.notify_service.payload.request.SearchTemplatesRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, String> {

    @Query(value = "select nt " +
            "from NotificationTemplate nt " +
            "where (coalesce(:#{#request.templateCode}, null) is null or nt.templateCode like %:#{#request.templateCode}%) " +
            "and (coalesce(:#{#request.channel}, null) is null or nt.channel = :#{#request.channel}) " +
            "and (coalesce(:#{#request.title}, null) is null or nt.title like %:#{#request.title}%) " +
            "and (coalesce(:#{#request.isActive}, null) is null or nt.isActive = :#{#request.isActive})")
    Page<NotificationTemplate> searchTemplates(@Param("request") SearchTemplatesRequest request, Pageable pageable);

    NotificationTemplate findNotificationTemplateByTemplateCodeAndIsActive(String templateCode, Boolean isActive);
}