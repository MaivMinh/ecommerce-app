package com.minh.realtime_gateway.DTOs;

import lombok.*;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SessionInfo implements Serializable {
    private String id;
    private String principal;
    private Map<String, Object> attributes;
    private String uri;
}
