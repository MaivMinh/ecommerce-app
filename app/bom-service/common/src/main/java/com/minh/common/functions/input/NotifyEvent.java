package com.minh.common.functions.input;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotifyEvent {
    private String templateCode;
    private Map<String, String> recipient;
    private Map<String, Object> metaData;
}
