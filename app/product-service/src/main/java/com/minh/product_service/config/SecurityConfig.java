package com.minh.product_service.config;

import com.minh.common.interceptor.AuthorizationInterceptorFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig {
    @Value("${API_GATEWAY_DOMAIN:http://localhost:8080}")
    private String apiGatewayDomain;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> {
            request
                    .anyRequest().authenticated();
        });
        http.csrf(AbstractHttpConfigurer::disable);
        http.addFilterAfter(new AuthorizationInterceptorFilter(), BasicAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        .addMapping("/**")
                        .allowedOrigins(apiGatewayDomain) // only API Gateway.
                        .allowCredentials(false)
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .maxAge(3600)
                        .exposedHeaders("*");
            }
        };
    }
}
