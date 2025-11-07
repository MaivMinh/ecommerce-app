package com.minh.common.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.common.DTOs.AuthenticatedDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AuthorizationInterceptorFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = this.extractToken(request);
        if (token != null) {
            try {
                UserDetails userDetails = this.extractUser(token);
                AuthenticatedDetails details = this.extractDetails(token);
                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
                    authentication.setDetails(details);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    SecurityContextHolder.clearContext();
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                    return;
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private AuthenticatedDetails extractDetails(String token) {
        // Parse JWT token and extract user information
        try {
            String[] parts = token.split("\\.");

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> payload = mapper.readValue(payloadJson, Map.class);

            String givenName = (String) payload.get("given_name");
            String familyName = (String) payload.get("family_name");
            String email = (String) payload.get("email");

            AuthenticatedDetails details = new AuthenticatedDetails();
            details.setGivenName(givenName);
            details.setFamilyName(familyName);
            details.setEmail(email);
            return details;
        } catch (Exception e) {
            return null;
        }
    }

    private UserDetails extractUser(String token) {
        // Parse JWT token and extract user information
        try {
            String[] parts = token.split("\\.");

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> payload = mapper.readValue(payloadJson, Map.class);

            String username = (String) payload.get("preferred_username");

            List<String> roles = new ArrayList<>();
            if (payload.containsKey("resource_access")) {
                Map<String, Object> resourceAccess = (Map<String, Object>) payload.get("resource_access");
                if (resourceAccess.containsKey("e-commerce")) {
                    Map<String, Object> ecommerce = (Map<String, Object>) resourceAccess.get("e-commerce");
                    if (ecommerce.containsKey("roles")) {
                        roles = (List<String>) ecommerce.get("roles");
                    }
                }
            }

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());

            return new org.springframework.security.core.userdetails.User(
                    username, "", authorities);
        } catch (Exception e) {
            logger.error("Error parsing JWT token", e);
            return null;
        }
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return (path.contains("swagger-ui") || path.contains("v3/api-docs") || path.contains("actuator"));
    }
}
