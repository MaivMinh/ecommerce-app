package com.minh.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Value("${CLIENT_DOMAIN:http://localhost:5173}")
    private String clientDomain;

    @Value("${ADMIN_DOMAIN:http://localhost:5174}")
    private String adminDomain;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .pathMatchers(getOpenAPIDoc()).permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(resource -> resource  /// resource.jwt() => Format access token dưới dạng JWT.
                        .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(
                                new ReactiveJwtAuthenticationConverterAdapter(new KeycloakRoleConverter())
                        ))
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }

    public String[] getOpenAPIDoc() {
        List<String> lstUrl = new ArrayList<>();
        lstUrl.add("/swagger-ui/**");
        lstUrl.add("/v3/api-docs/**");
        lstUrl.add("/swagger-ui/index.html");
        lstUrl.add("/webjars/**");
        lstUrl.add("/api/v1/recruitment/position/**");
        lstUrl.add("/api/v1/recruitment/position-class/**");
        lstUrl.add("/api/v1/recruitment/position-group/**");
        lstUrl.add("/api/v1/recruitment/location/**");
        lstUrl.add("/api/v1/recruitment/job-ad/**");
        lstUrl.add("/api/v1/recruitment/candidate/apply");
        lstUrl.add("/api/v1/recruitment/auth/login");
        lstUrl.add("/actuator/**");
        return lstUrl.toArray(new String[0]);
    }

    /// Cors configuration.
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(false);
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedOrigins(List.of(clientDomain, adminDomain));
        config.setExposedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}
