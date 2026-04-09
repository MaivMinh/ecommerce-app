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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.List;

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
                .discountPercentage(vr.getDiscountPercentage())
                .value(vr.getValue())
                .maxValue(vr.getMaxValue())
                .build();

        return modelMapper.map(playerVoucherRepository.save(entity), PlayerVoucherResponse.class);
    }

    @Override
    @Transactional
    public List<PlayerVoucherResponse> assignVoucherToUserBatch(List<PlayerVoucher> data) {
        if (CollectionUtils.isEmpty(data)) return List.of();
        List<PlayerVoucher> savedEntities = playerVoucherRepository.saveAll(data);
        return savedEntities.stream()
                .map(entity -> modelMapper.map(entity, PlayerVoucherResponse.class))
                .toList();
    }
}
