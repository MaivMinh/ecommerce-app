package com.minh.realtime_gateway.service.impl;

import com.minh.realtime_gateway.service.KeycloakIntrospectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakIntrospectiveServiceImpl implements KeycloakIntrospectionService {
    private final WebClient webClient;

    @Value("${keycloak.realm:e-commerce}")
    private String realm;

    @Value("${keycloak.credentials.client-id:ecommerce-backend}")
    private String clientId;

    @Value("${keycloak.credentials.client-secret:rDBwKuMWu9ZdKwrDujdzv26s1flOdEpI}")
    private String clientSecret;

    public Boolean introspect(String token) {
        log.info("Requesting token introspection for token: {}", token);
        if (!StringUtils.hasText(token)) {
            return false;
        }

        String basicAuth = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes());

        Map<String, Object> response = webClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token/introspect", realm)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth)
                .body(BodyInserters.fromFormData("token", token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        return this.isTokenValid(response);
    }

    private boolean isTokenValid(Map<String, Object> response) {
        return Boolean.TRUE.equals(response.get("active"));
    }

    public String getUsername(Map<String, Object> response) {
        return (String) response.get("username");
    }
}
