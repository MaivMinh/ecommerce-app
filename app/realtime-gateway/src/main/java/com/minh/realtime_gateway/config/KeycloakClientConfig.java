package com.minh.realtime_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class KeycloakClientConfig {

    @Value("${keycloak.server-url:http://localhost:6060}")
    private String keycloakServerUrl;

    @Bean
    WebClient keycloakWebClient() {
        return WebClient.builder()
                .baseUrl(keycloakServerUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }
}
