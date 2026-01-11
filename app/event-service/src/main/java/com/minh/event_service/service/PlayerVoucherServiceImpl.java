package com.minh.event_service.service;

import com.minh.common.utils.AppUtils;
import com.minh.event_service.DTO.UserScoreData;
import com.minh.event_service.entity.PlayerVoucher;
import com.minh.event_service.payload.response.PlayerVoucherResponse;
import com.minh.event_service.payload.response.VoucherResponse;
import com.minh.event_service.repository.PlayerVoucherRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PlayerVoucherServiceImpl implements PlayerVoucherService {
    private final PlayerVoucherRepository playerVoucherRepository;
    private final ModelMapper modelMapper;

    @Override
    public PlayerVoucherResponse assignVoucherToUser(UserScoreData usd, VoucherResponse vr) {
        PlayerVoucher entity = PlayerVoucher.builder()
                .id(AppUtils.generateUUIDv7())
                .voucherId(vr.getId())
                .campaignId(vr.getCampaignId())
                .code(vr.getCode())
                .redeemedAt(Instant.now())
                .used(Boolean.FALSE)
                .username(usd.getUsername())
                .build();

        return modelMapper.map(playerVoucherRepository.save(entity), PlayerVoucherResponse.class);
    }
}
