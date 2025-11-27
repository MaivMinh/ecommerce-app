package com.minh.event_service.service.impl;

import com.minh.common.constants.ErrorCode;
import com.minh.common.constants.ResponseMessages;
import com.minh.common.message.MessageCommon;
import com.minh.common.response.ResponseData;
import com.minh.common.utils.AppUtils;
import com.minh.event_service.entity.Campaign;
import com.minh.event_service.payload.request.CreateCampaignRequest;
import com.minh.event_service.payload.request.SearchCampaignsRequest;
import com.minh.event_service.payload.request.UpdateCampaignRequest;
import com.minh.event_service.payload.response.CampaignResponse;
import com.minh.event_service.payload.response.GameResponse;
import com.minh.event_service.repository.CampaignRepository;
import com.minh.event_service.service.CampaignService;
import com.minh.event_service.service.GameService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final ModelMapper modelMapper;
    private final CampaignRepository campaignRepository;
    private final MessageCommon messageCommon;
    private final GameService gameService;

    @Override
    public void createCampaign(CreateCampaignRequest request) {
        Campaign campaign = new Campaign();
        modelMapper.map(request, campaign);
        campaign.setId(AppUtils.generateUUIDv7());
        campaignRepository.save(campaign);
    }

    @Override
    public ResponseData searchCampaigns(SearchCampaignsRequest request) {
        Pageable pageable = AppUtils.toPageable(request);

        Page<Campaign> pagedCampaigns = campaignRepository.searchCampaigns(request, pageable);
        Page<CampaignResponse> pagedCampaignRes = pagedCampaigns.map(campaign -> modelMapper.map(campaign, CampaignResponse.class));

        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(pagedCampaignRes)
                .build();
    }

    @Override
    public ResponseData getCampaignDetailById(String id) {
        if (!StringUtils.hasText(id)) {
            return ResponseData.builder()
                    .status(400)
                    .message(messageCommon.getMessage(ErrorCode.INVALID_PARAMS))
                    .data(null)
                    .build();
        }

        Campaign campaign = campaignRepository.findById(id).orElseThrow(
                () -> new RuntimeException(messageCommon.getMessage(ErrorCode.Campaign.NOT_FOUND, id))
        );

        CampaignResponse campaignResponse = modelMapper.map(campaign, CampaignResponse.class);
        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(campaignResponse)
                .build();
    }

    @Override
    public void updateCampaign(UpdateCampaignRequest request) {
        Campaign saved = campaignRepository.findById(request.getId()).orElseThrow(
                () -> new RuntimeException(messageCommon.getMessage(ErrorCode.Campaign.NOT_FOUND, request.getId()))
        );

        GameResponse game = gameService.getGameDetailById(request.getGameId());
        if (Objects.isNull(game)) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.Game.NOT_FOUND, request.getGameId()));
        }
        modelMapper.map(request, saved);
        campaignRepository.save(saved);
    }

    @Override
    public void deleteCampaign(String id) {
        Campaign saved = campaignRepository.findById(id).orElseThrow(
                () -> new RuntimeException(messageCommon.getMessage(ErrorCode.Campaign.NOT_FOUND, id))
        );
        campaignRepository.deleteById(saved.getId());
    }
}