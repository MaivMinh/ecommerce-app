package com.minh.event_service.service;

import com.minh.common.response.ResponseData;
import com.minh.event_service.payload.request.CreateVoucherRequest;
import com.minh.event_service.payload.request.SearchVouchersRequest;
import com.minh.event_service.payload.request.UpdateVoucherRequest;
import com.minh.event_service.payload.request.VoucherRequest;
import com.minh.event_service.payload.response.VoucherResponse;
import event_service.UpdateVoucherResponse;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface VoucherService {
    void createVoucher(@Valid CreateVoucherRequest request);

    ResponseData searchVouchers(SearchVouchersRequest request);

    ResponseData getVoucherDetailById(String id);

    void updateVoucher(UpdateVoucherRequest request);

    void deleteVoucher(String id);

    void createVouchersBatch(List<VoucherRequest> vouchers, String campaignId);

    List<VoucherResponse> getVouchersByCampaignId(String id);

    void deleteBatch(List<VoucherResponse> values);

    void saveAllUpdatedVouchers(List<UpdateVoucherRequest> updatedVouchers);

    void deleteVouchersByCampaignId(String id);

    /**
     * Danh sách các voucher mà người dùng đã thu thập được.
     * @param username: Tên đăng nhập của người dùng.
     * @return: Danh sách các voucher của người dùng.
     */
    ResponseData redeemVoucher(String username);

    /**
     * Phương thức thực hiện cập nhật lại voucher nhưng thông qua gRPC call.
     * @param request: Chứa voucher id và username.
     * @return: Kết quả cập nhật.
     */
    UpdateVoucherResponse updateVoucherGrpc(event_service.UpdateVoucherRequest request);
}
