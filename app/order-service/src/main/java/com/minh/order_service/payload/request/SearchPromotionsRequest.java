package com.minh.order_service.payload.request;

import com.minh.common.DTOs.SearchDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchPromotionsRequest extends SearchDTO {
    private String code;
    private String status;
}
