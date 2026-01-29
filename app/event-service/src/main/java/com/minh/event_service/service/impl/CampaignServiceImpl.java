package com.minh.event_service.service.impl;

import com.minh.common.constants.ErrorCode;
import com.minh.common.constants.ResponseMessages;
import com.minh.common.message.MessageCommon;
import com.minh.common.response.ResponseData;
import com.minh.common.utils.AppUtils;
import com.minh.event_service.entity.Campaign;
import com.minh.event_service.enums.GameEventType;
import com.minh.event_service.payload.request.*;
import com.minh.event_service.payload.response.*;
import com.minh.event_service.repository.CampaignImageRepository;
import com.minh.event_service.repository.CampaignRepository;
import com.minh.event_service.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final ModelMapper modelMapper;
    private final CampaignRepository campaignRepository;
    private final MessageCommon messageCommon;
    private final GameService gameService;
    private final QuestionService questionService;
    private final VoucherService voucherService;
    private final CampaignImageService campaignImageService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CampaignImageRepository campaignImageRepository;
    private final EventTimelineScheduler eventTimelineScheduler;

    @Override
    @Transactional
    public void createCampaign(CreateCampaignRequest request) {
        /// Kiểm tra có tồn tại question collection không
        com.minh.common.response.ResponseData res = questionService.getQuestionCollectionById(request.getQuestionCollectionId());
        if (Objects.isNull(res.getData())) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.QuestionCollection.NOT_FOUND, request.getQuestionCollectionId()));
        }

        Campaign campaign = new Campaign();
        modelMapper.map(request, campaign);
        campaign.setId(AppUtils.generateUUIDv7());
        Campaign saved = campaignRepository.save(campaign);

        /// Thực hiện tạo mới các voucher.
        List<VoucherRequest> vouchers = request.getVouchers();
        voucherService.createVouchersBatch(vouchers, saved.getId());

        /// Thực hiện lưu ảnh cho campaign.
        campaignImageService.saveCampaignImagesBatch(request.getImageUrls(), saved.getId());

        eventTimelineScheduler.scheduleCampaign(saved);
    }


    @Override
    public ResponseData searchCampaigns(SearchCampaignsRequest request) {
        Pageable pageable = AppUtils.toPageable(request);

        Page<Campaign> pagedCampaigns = campaignRepository.searchCampaigns(request, pageable);
        Page<CampaignResponse> pagedCampaignRes = pagedCampaigns.map(campaign -> {
            CampaignResponse campaignResponse = modelMapper.map(campaign, CampaignResponse.class);
            /// Lấy danh sách các voucher.
            List<VoucherResponse> vouchers = voucherService.getVouchersByCampaignId(campaign.getId());
            campaignResponse.setVouchers(vouchers);
            return campaignResponse;
        });


        /// Lấy danh sách các ảnh cho các campaign.
        List<String> campaignIds = pagedCampaigns.stream().map(Campaign::getId).toList();
        Map<String, List<CampaignImageResponse>> campaignImagesMap = campaignImageService.getCampaignImagesByCampaignIds(campaignIds);
        pagedCampaignRes.forEach(campaignRes -> {
            List<CampaignImageResponse> images = campaignImagesMap.get(campaignRes.getId());
            campaignRes.setCampaignImages(images);
        });

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

        CampaignDetailResponse campaignResponse = modelMapper.map(campaign, CampaignDetailResponse.class);
        List<VoucherResponse> vouchers = voucherService.getVouchersByCampaignId(campaign.getId());
        campaignResponse.setVouchers(vouchers);

        /// Lấy thông tin game.
        GameResponse game = gameService.getGameDetailById(campaign.getGameId());
        campaignResponse.setGame(game);

        /// Lấy tên bộ câu hỏi.
        ResponseData questionCollectionRes = questionService.getQuestionCollectionById(campaign.getQuestionCollectionId());
        if (!Objects.isNull(questionCollectionRes.getData())) {
            QuestionCollectionResponse questionCollection = (QuestionCollectionResponse) questionCollectionRes.getData();
            campaignResponse.setQuestionCollectionName(questionCollection.getTitle());
        }

        /// Lấy danh sách ảnh cho campaign
        List<String> campaignIds = List.of(campaign.getId());
        Map<String, List<CampaignImageResponse>> campaignImagesMap = campaignImageService.getCampaignImagesByCampaignIds(campaignIds);
        List<CampaignImageResponse> images = campaignImagesMap.get(campaign.getId());
        campaignResponse.setCampaignImages(images);

        /// Kiểm tra xem người dùng đã tham gia sự kiện chưa.
        String currentUser = AppUtils.getUsername();
        String redisKeyCheck = "event:attendance:" + campaign.getId() + ":user:" + currentUser;
        Boolean isRegistered = redisTemplate.hasKey(redisKeyCheck);
        campaignResponse.setIsRegistered(isRegistered);

        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(campaignResponse)
                .build();
    }

    @Override
    @Transactional
    public void updateCampaign(UpdateCampaignRequest request) {
        Campaign saved = campaignRepository.findById(request.getId()).orElseThrow(
                () -> new RuntimeException(messageCommon.getMessage(ErrorCode.Campaign.NOT_FOUND, request.getId()))
        );

        GameResponse game = gameService.getGameDetailById(request.getGameId());
        if (Objects.isNull(game)) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.Game.NOT_FOUND, request.getGameId()));
        }

        /// Update vouchers.
        List<VoucherRequest> vouchers = request.getVouchers();
        List<UpdateVoucherRequest> updatedVouchers = new ArrayList<>();
        List<VoucherResponse> existingVouchers = voucherService.getVouchersByCampaignId(saved.getId());
        Map<String, VoucherResponse> vouchersMap = existingVouchers.stream()
                .collect(java.util.stream.Collectors.toMap(VoucherResponse::getId, v -> v));

        for (VoucherRequest voucher : vouchers) {
            if (vouchersMap.containsKey(voucher.getId())) {
                /// Nếu tồn tại, thực hiện cập nhật. Sau đó xóa khỏi map để đánh dấu đã xử lý.
                VoucherResponse existingVoucher = vouchersMap.get(voucher.getId());
                modelMapper.map(voucher, existingVoucher);
                UpdateVoucherRequest updateVoucherRequest = modelMapper.map(existingVoucher, UpdateVoucherRequest.class);
                updatedVouchers.add(updateVoucherRequest);
                vouchersMap.remove(voucher.getId());
            } else {
                /// Nếu không tồn tại, thực hiện tạo mới.
                UpdateVoucherRequest newVoucher = new UpdateVoucherRequest();
                modelMapper.map(voucher, newVoucher);
                newVoucher.setId(AppUtils.generateUUIDv7());
                newVoucher.setCampaignId(request.getId());
                updatedVouchers.add(newVoucher);
            }
        }

        /// Nếu map khác rỗng, nghĩa là có voucher bị xóa. Thực hiện xóa.
        if (!vouchersMap.isEmpty()) {
            voucherService.deleteBatch(vouchersMap.values().stream().toList());
        }

        /// Cập nhật lại toàn bộ.
        voucherService.saveAllUpdatedVouchers(updatedVouchers);

        ResponseData responseData = questionService.getQuestionCollectionById(request.getQuestionCollectionId());
        if (Objects.isNull(responseData.getData())) {
            throw new RuntimeException(messageCommon.getMessage(ErrorCode.QuestionCollection.NOT_FOUND, request.getQuestionCollectionId()));
        }

        /// Cập nhật hình ảnh cho Voucher.
        List<CampaignImageRequest> campaignImageRequests = request.getCampaignImages();
        Map<String, List<CampaignImageResponse>> savedCampaignImagesMap = campaignImageService.getCampaignImagesByCampaignIds(List.of(request.getId()));
        List<CampaignImageResponse> savedCampaignImages = savedCampaignImagesMap.getOrDefault(request.getId(), new ArrayList<>());
        List<String> newImageUrls = new ArrayList<>();
        if (!savedCampaignImages.isEmpty()) {
            Map<String, CampaignImageResponse> savedCampaignImagesMapById = savedCampaignImages.stream()
                    .collect(java.util.stream.Collectors.toMap(CampaignImageResponse::getId, img -> img));
            for (CampaignImageRequest imgReq : campaignImageRequests) {
                if (!StringUtils.hasText(imgReq.getId())) {
                    newImageUrls.add(imgReq.getImageUrl());
                } else {
                    savedCampaignImagesMapById.remove(imgReq.getId());
                }
            }

            // Xóa các ảnh không còn trong request
            if (!savedCampaignImagesMapById.isEmpty()) {
                List<String> imagesToDelete = new ArrayList<>();
                for (CampaignImageResponse imgRes : savedCampaignImagesMapById.values()) {
                    imagesToDelete.add(imgRes.getId());
                }
                campaignImageService.deleteAllById(imagesToDelete);
            }
        } else {
            // Nếu không có ảnh lưu trong db, thêm tất cả ảnh từ request
            newImageUrls = campaignImageRequests.stream()
                    .map(CampaignImageRequest::getImageUrl)
                    .toList();
        }
        campaignImageService.saveCampaignImagesBatch(newImageUrls, request.getId());

        Instant reqStartTime = request.getStartTime();
        Instant savedStartTime = saved.getStartTime();
        modelMapper.map(request, saved);
        campaignRepository.save(saved);

        if (reqStartTime.compareTo(savedStartTime) != 0 && reqStartTime.isAfter(Instant.now())) {
            eventTimelineScheduler.scheduleCampaign(saved);
        }
    }

    @Override
    @Transactional
    public void deleteCampaign(String id) {
        Campaign saved = campaignRepository.findById(id).orElseThrow(
                () -> new RuntimeException(messageCommon.getMessage(ErrorCode.Campaign.NOT_FOUND, id))
        );

        /// Xóa tất cả voucher liên quan đến campaign
        voucherService.deleteVouchersByCampaignId(saved.getId());

        /// Xóa các ảnh liên quan đến campaign.
        campaignImageService.deleteAllByCampaignId(saved.getId());

        campaignRepository.deleteById(saved.getId());
    }
}