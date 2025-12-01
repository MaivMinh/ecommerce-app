package com.minh.event_service.service.impl;

import com.minh.common.utils.AppUtils;
import com.minh.event_service.entity.CampaignImage;
import com.minh.event_service.payload.request.CampaignImageRequest;
import com.minh.event_service.payload.response.CampaignImageResponse;
import com.minh.event_service.repository.CampaignImageRepository;
import com.minh.event_service.service.CampaignImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CampaignImageServiceImpl implements CampaignImageService {

    private final CampaignImageRepository campaignImageRepository;

    @Override
    public void saveCampaignImagesBatch(List<String> campaignImages, String campaignId) {
        if (campaignImages.isEmpty()) return;
        // Lưu batch ảnh cho campaign
        List<CampaignImage> images = campaignImages.stream()
                .map(campaignImage -> {
                    CampaignImage img = new CampaignImage();
                    img.setId(AppUtils.generateUUIDv7());
                    img.setCampaignId(campaignId);
                    img.setImageUrl(campaignImage);
                    return img;
                })
                .toList();

        campaignImageRepository.saveAll(images);
    }

    @Override
    public Map<String, List<CampaignImageResponse>> getCampaignImagesByCampaignIds(List<String> campaignIds) {
        if (campaignIds.isEmpty()) {
            return Map.of();
        }
        List<CampaignImage> images = campaignImageRepository.getCampaignImagesByCampaignIds(campaignIds);

        Map<String, List<CampaignImageResponse>> groupedImages = new HashMap<>();
        images.forEach(
                image -> {
                    String campaignId = image.getCampaignId();
                    if (!groupedImages.containsKey(campaignId)) {
                        groupedImages.put(campaignId, new ArrayList<>());
                    } else {
                        groupedImages.get(campaignId).add(CampaignImageResponse.builder()
                                .id(image.getId())
                                .campaignId(image.getCampaignId())
                                .imageUrl(image.getImageUrl())
                                .build());
                    }
                }
        );

        return groupedImages;
    }

    @Override
    public void deleteAllById(List<String> imagesToDelete) {
        if (imagesToDelete.isEmpty()) return;
        campaignImageRepository.deleteAllByIdInBatch(imagesToDelete);
    }


    @Override
    public void deleteAllByCampaignId(String id) {
        if (!StringUtils.hasText(id)) return;
        campaignImageRepository.deleteAllByCampaignId(id);
    }
}
