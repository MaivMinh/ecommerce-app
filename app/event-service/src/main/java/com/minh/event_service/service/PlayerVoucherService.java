package com.minh.event_service.service;

import com.minh.event_service.DTO.UserScoreData;
import com.minh.event_service.entity.PlayerVoucher;
import com.minh.event_service.payload.response.PlayerVoucherResponse;
import com.minh.event_service.payload.response.VoucherResponse;

import java.util.List;

public interface PlayerVoucherService {
    /**
     * Gán voucher cho người chơi dựa trên dữ liệu điểm số của họ.
     * @param usd: Dữ liệu điểm số của người chơi.
     * @param vr: Thông tin voucher cần gán.
     */
    PlayerVoucherResponse assignVoucherToUser(UserScoreData usd, VoucherResponse vr);

    List<PlayerVoucherResponse> assignVoucherToUserBatch(List<PlayerVoucher> data);
}
