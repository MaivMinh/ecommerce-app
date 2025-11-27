package com.minh.event_service.service.impl;

import com.minh.common.constants.ErrorCode;
import com.minh.common.message.MessageCommon;
import com.minh.common.response.ResponseData;
import com.minh.common.utils.AppUtils;
import com.minh.event_service.entity.Attend;
import com.minh.event_service.entity.Campaign;
import com.minh.event_service.grpc.client.SupportGrpcClient;
import com.minh.event_service.payload.request.CreateAttendEventRequest;
import com.minh.event_service.payload.request.SearchAttendsEventRequest;
import com.minh.event_service.payload.request.UpdateAttendEventRequest;
import com.minh.event_service.payload.response.AttendResponse;
import com.minh.event_service.repository.AttendRepository;
import com.minh.event_service.service.AttendService;
import com.minh.event_service.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import support_service.VerifyUserRequest;
import support_service.VerifyUserResponse;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AttendServiceImpl implements AttendService {
    private final SupportGrpcClient supportGrpcClient;
    private final CampaignService campaignService;
    private final ModelMapper modelMapper;
    private final AttendRepository attendRepository;
    private final MessageCommon messageCommon;

    @Override
    public void createAttendEvent(CreateAttendEventRequest request) {
        /// Xác thực thông tin của username.
        try {
            VerifyUserRequest req = VerifyUserRequest.newBuilder()
                    .setUsername(request.getUsername())
                    .build();
            VerifyUserResponse res = supportGrpcClient.verifyUser(req);
            if (res.getStatus() != 200) {
                throw new RuntimeException("Username không tồn tại!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        /// Xác thực thông tin của campaign.
        String campaignId = request.getCampaignId();
        ResponseData responseData = campaignService.getCampaignDetailById(campaignId);
        if (Objects.isNull(responseData.getData()))    {
            throw new RuntimeException("Campaign không tồn tại!");
        }

        /// Tạo mới thông tin attend.
        Attend attend = new Attend();
        modelMapper.map(request, attend);
        attend.setId(AppUtils.generateUUIDv7());
        attendRepository.save(attend);
    }

    @Override
    public ResponseData searchAttendsEvent(SearchAttendsEventRequest request) {
        Pageable pageable = AppUtils.toPageable(request);

        Page<Attend> pagedAttends = attendRepository.searchAttendsEvent(request, pageable);
        Page<AttendResponse> attendDTOPage = pagedAttends.map(attend -> modelMapper.map(attend, AttendResponse.class));
        return ResponseData.builder()
                .status(200)
                .message("Success")
                .data(attendDTOPage)
                .build();
    }

    @Override
    public void updateAttendEvent(UpdateAttendEventRequest request) {
        Attend saved = attendRepository.findById(request.getId()).orElseThrow(
                () -> new RuntimeException(messageCommon.getMessage(ErrorCode.Attend.NOT_FOUND, request.getId()))
        );

        modelMapper.map(request, saved);
        attendRepository.save(saved);
    }
}