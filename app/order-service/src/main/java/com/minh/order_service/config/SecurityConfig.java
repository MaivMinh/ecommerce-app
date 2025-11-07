package com.minh.order_service.config;

import com.minh.common.interceptor.AuthorizationInterceptorFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> {
                    request
                            .requestMatchers("/actuator/**").permitAll()
                            .anyRequest().authenticated();
                });
        http.csrf(AbstractHttpConfigurer::disable);
        http.addFilterAfter(new AuthorizationInterceptorFilter(), BasicAuthenticationFilter.class);
        return http.build();
    }
}
