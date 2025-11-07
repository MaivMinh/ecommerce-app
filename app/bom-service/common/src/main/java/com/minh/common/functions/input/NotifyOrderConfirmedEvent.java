package com.minh.common.functions.input;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotifyOrderConfirmedEvent {
    private String templateCode;
    private Map<String, String> recipient;
    private NotifyOrderConfirmedParams params;
    private Map<String, Object> metaData;
}