package com.minh.common.utils;

import com.github.f4b6a3.uuid.UuidCreator;
import com.minh.common.DTOs.AuthenticatedDetails;
import com.minh.common.DTOs.SearchDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
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
        if (StringUtils.hasText(searchDTO.getSortBy())) {
            int page = Math.max(0, searchDTO.getPage() - 1);
            int size = Math.max(1, searchDTO.getSize());
            List<Sort.Order> orders = new ArrayList<>();
            String[] sortParams = searchDTO.getSortBy().split(",");
            for (String param: sortParams) {
                orders.add(new Sort.Order(Sort.Direction.fromString(StringUtils.hasLength(searchDTO.getSortDirection()) ? searchDTO.getSortDirection() : "ASC"), param.trim()));
            }
            return PageRequest.of(page, size, Sort.by(orders));
        }
        int page = Math.max(0, searchDTO.getPage() - 1);
        int size = Math.max(1, searchDTO.getSize());
        return PageRequest.of(page, size);
    }

    public static String generateUUIDv7() {
        return UuidCreator.getTimeOrderedEpoch().toString();
    }
}
