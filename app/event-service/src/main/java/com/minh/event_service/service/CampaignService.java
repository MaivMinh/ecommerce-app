package com.minh.event_service.service;

import com.minh.common.response.ResponseData;
import com.minh.event_service.payload.request.CreateCampaignRequest;
import com.minh.event_service.payload.request.SearchCampaignsRequest;
import com.minh.event_service.payload.request.UpdateCampaignRequest;
import jakarta.validation.Valid;

public interface CampaignService {

    void createCampaign(@Valid CreateCampaignRequest request);

    ResponseData searchCampaigns(SearchCampaignsRequest request);

    ResponseData getCampaignDetailById(String id);

    void updateCampaign(@Valid UpdateCampaignRequest request);

    void deleteCampaign(String id);
}
