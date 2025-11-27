package com.minh.support_service.service;


import support_service.*;

public interface SupportService {

    GetShippingAddressResponse getShippingAddress(GetShippingAddressRequest request);

    GetUserInfoResponse getUserInfo(GetUserInfoRequest request);

    /**
     * Hàm thực hiện kiểm tra xem username có tồn tại hay không.
     * @param request: VerifyUserRequest chứa thông tin username cần kiểm tra.
     * @return: VerifyUserResponse chứa kết quả kiểm tra.
     */
    VerifyUserResponse verifyUser(VerifyUserRequest request);
}
