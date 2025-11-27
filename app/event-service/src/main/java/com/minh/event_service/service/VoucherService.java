package com.minh.event_service.service;

import com.minh.common.response.ResponseData;
import com.minh.event_service.payload.request.CreateVoucherRequest;
import com.minh.event_service.payload.request.SearchVouchersRequest;
import com.minh.event_service.payload.request.UpdateVoucherRequest;
import jakarta.validation.Valid;

public interface VoucherService {
    void createVoucher(@Valid CreateVoucherRequest request);

    ResponseData searchVouchers(SearchVouchersRequest request);

    ResponseData getVoucherDetailById(String id);

    void updateVoucher(UpdateVoucherRequest request);

    void deleteVoucher(String id);
}
