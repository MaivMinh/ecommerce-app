package com.minh.product_service.payload.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindProductBySlugQuery {
    private String slug;
}
