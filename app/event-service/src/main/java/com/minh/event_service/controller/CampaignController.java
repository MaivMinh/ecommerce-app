package com.minh.event_service.controller;

import com.minh.common.constants.ResponseMessages;
import com.minh.common.response.ResponseData;
import com.minh.event_service.payload.request.CreateCampaignRequest;
import com.minh.event_service.payload.request.SearchCampaignsRequest;
import com.minh.event_service.payload.request.UpdateCampaignRequest;
import com.minh.event_service.service.CampaignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/campaigns")
@RestController
@Validated
@RequiredArgsConstructor
public class CampaignController {
    private final CampaignService campaignService;

    @PostMapping(value = "")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> createCampaign(@RequestBody @Valid CreateCampaignRequest request) {
        campaignService.createCampaign(request);
        return ResponseEntity.ok(ResponseData.builder()
                .status(200)
                .message("Success")
                .data(null)
                .build());
    }

    @PostMapping(value = "/search")
    public ResponseEntity<ResponseData> searchCampaigns(@RequestBody SearchCampaignsRequest request) {
        ResponseData response = campaignService.searchCampaigns(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ResponseData> getCampaignDetailById(@PathVariable("id") String id) {
        ResponseData response = campaignService.getCampaignDetailById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(value = "")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> updateCampaign(@RequestBody @Valid UpdateCampaignRequest request) {
        campaignService.updateCampaign(request);
        return ResponseEntity.ok(ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(null)
                .build());
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> deleteCampaign(@PathVariable("id") String id) {
        campaignService.deleteCampaign(id);
        return ResponseEntity.ok(ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(null)
                .build());
    }
}