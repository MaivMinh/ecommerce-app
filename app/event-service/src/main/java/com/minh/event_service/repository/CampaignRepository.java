package com.minh.event_service.repository;

import com.minh.event_service.entity.Campaign;
import com.minh.event_service.payload.request.SearchCampaignsRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, String> {

    @Query(value = """
            select c from Campaign c
            where (coalesce(:#{#request.gameId}, null) is null or c.gameId = :#{#request.gameId} )
              and (coalesce(:#{#request.name}, null) is null or c.name like %:#{#request.name}%)
              and (c.startTime >= current_timestamp)
              and (coalesce(:#{#request.fromStartTime}, null) is null or c.startTime >= :#{#request.fromStartTime})
              and (coalesce(:#{#request.toStartTime}, null) is null or c.startTime <= :#{#request.toStartTime})
              and (coalesce(:#{#request.fromEndTime}, null) is null or c.endTime >= :#{#request.fromEndTime})
              and (coalesce(:#{#request.toEndTime}, null) is null or c.endTime <= :#{#request.toEndTime})
            """)
    Page<Campaign> searchCampaigns(@Param("request") SearchCampaignsRequest request, Pageable pageable);
}
