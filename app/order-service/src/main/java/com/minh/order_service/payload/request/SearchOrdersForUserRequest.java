package com.minh.order_service.payload.request;

import com.minh.common.DTOs.SearchDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchOrdersForUserRequest extends SearchDTO {
    private String keyword;
    private String status;
}
