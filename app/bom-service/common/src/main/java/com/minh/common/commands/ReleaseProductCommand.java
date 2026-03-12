package com.minh.common.commands;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReleaseProductCommand  extends SagaCommand{
    private String orderId;
    private String username;
    private String errorMsg;
}
