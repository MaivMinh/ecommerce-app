package com.minh.event_service.repository;

import com.minh.event_service.entity.CampaignImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignImageRepository extends JpaRepository<CampaignImage, String> {

    @Query(value = """
            select ci from CampaignImage ci where ci.campaignId in :campaignIds
            """)
    List<CampaignImage> getCampaignImagesByCampaignIds(@Param("campaignIds") List<String> campaignIds);

    @Modifying
    @Query(value = """
            delete from CampaignImage ci where ci.campaignId = :campaignId
            """)
    void deleteAllByCampaignId(@Param("campaignId") String campaignId);
}
