package com.minh.common.utils;

import com.github.f4b6a3.uuid.UuidCreator;
import com.minh.common.DTOs.AuthenticatedDetails;
import com.minh.common.DTOs.SearchDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class AppUtils {
    public static String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.nonNull(authentication) && authentication.isAuthenticated()) {
            return authentication.getName();
        }

        return null;
    }

    public static AuthenticatedDetails getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.nonNull(authentication) && authentication.isAuthenticated()) {
            Object details = authentication.getDetails();
            if (details instanceof AuthenticatedDetails) {
                return (AuthenticatedDetails) details;
            }
        }

        return null;
    }

    public static Pageable toPageable(SearchDTO searchDTO) {
        int page = Math.max(0, searchDTO.getPage() - 1);   // đảm bảo không âm
        int size = Math.max(1, searchDTO.getSize());   // đảm bảo size >= 1
        return PageRequest.of(page, size);
    }

    public static String generateUUIDv7() {
        return UuidCreator.getTimeOrderedEpoch().toString();
    }
}
