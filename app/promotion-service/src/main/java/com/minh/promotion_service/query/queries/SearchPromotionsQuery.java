package com.minh.promotion_service.query.queries;

import com.minh.common.DTOs.SearchDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchPromotionsQuery extends SearchDTO {
    private String code;
    private String status;
}
