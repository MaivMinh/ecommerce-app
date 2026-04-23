package com.minh.api_gateway.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakRoleConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access"); // Lấy roles từ realm_access
        List<String> roles = (List<String>) realmAccess.get("roles");

        var authorities = roles.stream()
                .map(role -> "ROLE_" + role) // Bắt buộc ROLE_ prefix để .hasRole() hoạt động
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        return new JwtAuthenticationToken(jwt, authorities);
    }
}
