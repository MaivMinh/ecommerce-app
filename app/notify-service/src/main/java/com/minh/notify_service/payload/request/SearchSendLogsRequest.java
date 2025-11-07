package com.minh.notify_service.payload.request;

import com.minh.common.DTOs.SearchDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchSendLogsRequest extends SearchDTO {
    private String templateCode;
    private String recipient;
    private String status;
    private String startDate;
    private String endDate;
}
