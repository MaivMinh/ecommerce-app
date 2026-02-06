package com.minh.realtime_gateway.service;

import java.util.Map;

public interface KeycloakIntrospectionService {
    Boolean introspect(String token);
    String getUsername(Map<String, Object> response);
}
