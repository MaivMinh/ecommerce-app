package com.minh.promotion_service.query.queries;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class GetPromotionsQuery {
    int page;
    int size;
}
