package com.minh.event_service.entity;

import com.minh.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "campaign_images")
@Getter
@Setter
public class CampaignImage extends BaseEntity {
    @Id
    private String id;
    private String campaignId;
    private String imageUrl;
}
