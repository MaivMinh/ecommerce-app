package com.minh.event_service.service.impl;

import com.minh.common.constants.ErrorCode;
import com.minh.common.constants.ResponseMessages;
import com.minh.common.message.MessageCommon;
import com.minh.common.response.ResponseData;
import com.minh.common.utils.AppUtils;
import com.minh.event_service.payload.response.VoucherRedeemedResponse;
import com.minh.event_service.entity.Voucher;
import com.minh.event_service.payload.request.CreateVoucherRequest;
import com.minh.event_service.payload.request.SearchVouchersRequest;
import com.minh.event_service.payload.request.UpdateVoucherRequest;
import com.minh.event_service.payload.request.VoucherRequest;
import com.minh.event_service.payload.response.VoucherResponse;
import com.minh.event_service.repository.VoucherRepository;
import com.minh.event_service.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final ModelMapper modelMapper;
    private final VoucherRepository voucherRepository;
    private final MessageCommon messageCommon;

    @Override
    public void createVoucher(CreateVoucherRequest request) {
        Voucher voucher = new Voucher();
        modelMapper.map(request, voucher);
        voucher.setId(AppUtils.generateUUIDv7());
        voucherRepository.save(voucher);
    }

    @Override
    public ResponseData searchVouchers(SearchVouchersRequest request) {
        Pageable pageable = AppUtils.toPageable(request);

        Page<Voucher> pagedVouchers = voucherRepository.searchVouchers(request, pageable);
        Page<VoucherResponse> res = pagedVouchers.map(voucher -> modelMapper.map(voucher, VoucherResponse.class));

        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(res)
                .build();
    }

    @Override
    public ResponseData getVoucherDetailById(String id) {
        Voucher voucher = voucherRepository.findById(id).orElseThrow(
                () -> new RuntimeException(messageCommon.getMessage(ErrorCode.Voucher.NOT_FOUND,id))
        );

        VoucherResponse voucherResponse = modelMapper.map(voucher, VoucherResponse.class);
        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(voucherResponse)
                .build();
    }

    @Override
    public void updateVoucher(UpdateVoucherRequest request) {
        Voucher saved = voucherRepository.findById(request.getId()).orElseThrow(
                () -> new RuntimeException(messageCommon.getMessage(ErrorCode.Voucher.NOT_FOUND, request.getId()))
        );

        modelMapper.map(request, saved);
        voucherRepository.save(saved);
    }

    @Override
    public void deleteVoucher(String id) {
        Voucher saved = voucherRepository.findById(id).orElseThrow(
                () -> new RuntimeException(messageCommon.getMessage(ErrorCode.Voucher.NOT_FOUND, id))
        );
        voucherRepository.delete(saved);
    }

    @Override
    public void createVouchersBatch(List<VoucherRequest> vouchers, String campaignId) {
        if (vouchers.isEmpty()) {
            return;
        }

        List<Voucher> entities = vouchers.stream()
                .map(voucherRequest -> {
                    Voucher voucher = new Voucher();
                    modelMapper.map(voucherRequest, voucher);
                    voucher.setId(AppUtils.generateUUIDv7());
                    voucher.setCampaignId(campaignId);
                    return voucher;
                })
                .toList();

        voucherRepository.saveAll(entities);
    }

    @Override
    public List<VoucherResponse> getVouchersByCampaignId(String id) {
        List<Voucher> vouchers = voucherRepository.getVouchersByCampaignId(id);
        return vouchers.stream()
                .map(voucher -> modelMapper.map(voucher, VoucherResponse.class))
                .toList();
    }

    @Override
    public void deleteBatch(List<VoucherResponse> values) {
        List<Voucher> vouchers = values.stream()
                .map(voucherResponse -> {
                    Voucher voucher = new Voucher();
                    modelMapper.map(voucherResponse, voucher);
                    return voucher;
                })
                .toList();
        voucherRepository.deleteAll(vouchers);
    }

    @Override
    public void saveAllUpdatedVouchers(List<UpdateVoucherRequest> updatedVouchers) {
        List<Voucher> vouchers = updatedVouchers.stream()
                .map(updateVoucherRequest -> {
                    Voucher voucher = new Voucher();
                    modelMapper.map(updateVoucherRequest, voucher);
                    voucher.setId(updateVoucherRequest.getId());
                    return voucher;
                })
                .toList();

        voucherRepository.saveAll(vouchers);
    }

    @Override
    public void deleteVouchersByCampaignId(String id) {
        voucherRepository.deleteVouchersByCampaignId(id);
    }

    @Override
    public ResponseData redeemVoucher(String username) {
        if (!StringUtils.hasText(username)) {
            return ResponseData.builder()
                    .status(400)
                    .message(messageCommon.getMessage(ErrorCode.INVALID_REQUEST))
                    .data(null)
                    .build();
        }

        List<Voucher> redeemedVouchers = voucherRepository.getVouchersByUsername(username);
        List<VoucherRedeemedResponse> voucherResponse = new ArrayList<>();
        for (Voucher voucher: redeemedVouchers) {
            VoucherRedeemedResponse res = new VoucherRedeemedResponse();
            res.setId(voucher.getId());
            res.setCode(voucher.getCode());
            if (Objects.isNull(voucher.getDiscountPercentage())) {
                res.setType("fixed");
                res.setDiscountValue(voucher.getValue());
            }   else {
                res.setType("percentage");
                res.setDiscountValue(voucher.getDiscountPercentage());
            }
            res.setMaxValue(voucher.getMaxValue());
            res.setEndDate(voucher.getExpirationDate().toString());
            res.setStatus(Instant.now().isAfter(voucher.getExpirationDate()) ? "inactive" : "active");
            voucherResponse.add(res);
        }

        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(voucherResponse)
                .build();
    }
}