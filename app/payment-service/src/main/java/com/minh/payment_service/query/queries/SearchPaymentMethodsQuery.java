package com.minh.payment_service.query.queries;

import com.minh.common.DTOs.SearchDTO;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchPaymentMethodsQuery extends SearchDTO {
    private String name;
    private String code;
    private String type;
    private Boolean isActive;
}
