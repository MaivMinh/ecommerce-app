package com.minh.support_service.service;

import support_service.GetShippingAddressRequest;
import support_service.GetShippingAddressResponse;
import support_service.GetUserInfoRequest;
import support_service.GetUserInfoResponse;

public interface SupportService {

    GetShippingAddressResponse getShippingAddress(GetShippingAddressRequest request);

    GetUserInfoResponse getUserInfo(GetUserInfoRequest request);
}
