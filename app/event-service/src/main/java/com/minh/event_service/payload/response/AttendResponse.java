package com.minh.event_service.payload.response;

import com.minh.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendResponse extends BaseEntity {
    private String id;
    private String username;
    private String campaignId;
    private String nickname;
    private Integer points;
}
