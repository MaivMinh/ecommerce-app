package com.minh.common.functions.input;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreatedEventInput {
    private String username;
    private String productId;
}
