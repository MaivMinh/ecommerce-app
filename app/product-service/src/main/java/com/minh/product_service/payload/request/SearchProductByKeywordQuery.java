package com.minh.product_service.payload.request;

import com.minh.common.DTOs.SearchDTO;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchProductByKeywordQuery extends SearchDTO {
    private String keyword;
}
