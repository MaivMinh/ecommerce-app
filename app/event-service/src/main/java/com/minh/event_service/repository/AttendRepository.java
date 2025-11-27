package com.minh.event_service.repository;

import com.minh.event_service.entity.Attend;
import com.minh.event_service.payload.request.SearchAttendsEventRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendRepository extends JpaRepository<Attend, String> {

    @Query(value = """
            select a from Attend a
            where (coalesce(:#{#request.campaignId}, null) is null or a.campaignId = :#{#request.campaignId})
            and (coalesce(:#{#request.username}, null) is null or a.username = :#{#request.username})
            """)
    Page<Attend> searchAttendsEvent(@Param("request") SearchAttendsEventRequest request, Pageable pageable);
}