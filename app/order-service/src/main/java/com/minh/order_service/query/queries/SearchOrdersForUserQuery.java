package com.minh.order_service.query.queries;

import com.minh.common.DTOs.SearchDTO;
import com.minh.order_service.enums.OrderStatus;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchOrdersForUserQuery extends SearchDTO {
    private String createdUser;
    private String keyword;
    private OrderStatus status;
}
