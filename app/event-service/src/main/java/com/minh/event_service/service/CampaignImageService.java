package com.minh.event_service.service;

import com.minh.event_service.entity.CampaignImage;
import com.minh.event_service.payload.request.CampaignImageRequest;
import com.minh.event_service.payload.response.CampaignImageResponse;

import java.util.List;
import java.util.Map;

public interface CampaignImageService {
    /**
     * Hàm thực hiện lưu toàn bộ ảnh cho một Campaign.
     * @param campaignImages: Danh sách ảnh cần lưu.
     */
    void saveCampaignImagesBatch(List<String> campaignImages, String campaignId);

    Map<String, List<CampaignImageResponse>> getCampaignImagesByCampaignIds(List<String> campaignIds);

    void deleteAllById(List<String> imagesToDelete);

    void deleteAllByCampaignId(String id);
}
