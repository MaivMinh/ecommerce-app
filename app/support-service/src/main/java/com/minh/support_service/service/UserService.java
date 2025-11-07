package com.minh.support_service.service;

import com.minh.common.response.ResponseData;
import com.minh.support_service.DTO.UserDto;

public interface UserService {
    ResponseData getProfile();

    ResponseData updateProfile(UserDto dto);

    UserDto findByUsername(String username);
//
//    GetShippingAddressResponse getShippingAddress(GetShippingAddressRequest request);
}
