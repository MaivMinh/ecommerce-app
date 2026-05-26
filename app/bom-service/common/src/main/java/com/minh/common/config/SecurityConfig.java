package com.minh.common.config;

import com.minh.common.interceptor.AuthorizationInterceptorFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> {
                    request
                            .requestMatchers("/actuator/**")
                            .permitAll()
                            .requestMatchers(org.springframework.http.HttpMethod.GET, "/ws/events", "/ws/events/**")
                            .permitAll()
                            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/payments/paypal/callback/**")
                            .permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/payments/paypal/callback/**")
                            .permitAll()
                            .anyRequest().authenticated();
                });
        http.csrf(AbstractHttpConfigurer::disable);
        http.addFilterAfter(new AuthorizationInterceptorFilter(), BasicAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
