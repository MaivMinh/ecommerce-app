package com.minh.notify_service.payload.request;

import com.minh.common.DTOs.SearchDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchTemplatesRequest extends SearchDTO {
    private String templateCode;
    private String channel; // e.g., EMAIL, SMS
    private Boolean isActive;
    private String title;
}
