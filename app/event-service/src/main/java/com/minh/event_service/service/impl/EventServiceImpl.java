package com.minh.event_service.service.impl;

import com.minh.common.constants.ErrorCode;
import com.minh.common.constants.ResponseMessages;
import com.minh.common.message.MessageCommon;
import com.minh.common.response.ResponseData;
import com.minh.common.utils.AppUtils;
import com.minh.event_service.payload.response.CampaignDetailResponse;
import com.minh.event_service.payload.response.CampaignResponse;
import com.minh.event_service.service.CampaignService;
import com.minh.event_service.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final CampaignService campaignService;
    private final MessageCommon messageCommon;

    @Override
    public ResponseData registerEventAttendance(String campaignId) {
        /// Xác định thông tin chiến dịch và người dùng đăng kí.
        ResponseData campaignResponse = campaignService.getCampaignDetailById(campaignId);
        if (Objects.isNull(campaignResponse.getData())) {
            return ResponseData.builder()
                    .status(404)
                    .message(messageCommon.getMessage(ErrorCode.Campaign.NOT_FOUND,campaignId))
                    .build();
        }

        /// Kiểm tra xem người dùng đã đăng kí tham gia sự kiện chưa.
        String currentUser = AppUtils.getUsername();
        String redisKeyCheck = "event:attendance:" + campaignId + ":user:" + currentUser;
        Boolean isRegistered = redisTemplate.hasKey(redisKeyCheck);
        if (isRegistered) {
            return ResponseData.builder()
                    .status(400)
                    .message(messageCommon.getMessage(ErrorCode.Event.ALREADY_REGISTERED, campaignId))
                    .build();
        }

        CampaignDetailResponse campaign = (CampaignDetailResponse) campaignResponse.getData();

        /// Xác định thời gian trò chơi bắt đầu.
        Instant startTime = campaign.getStartTime();
        Instant now = Instant.now();

        /// Tính toán thời gian hết hạn của key trong Redis.
        long expirationTimeInSeconds = startTime.getEpochSecond() - now.getEpochSecond() + 3600; // Cộng thêm 1 giờ sau khi trò chơi bắt đầu.

        /// Lưu thông tin đăng kí vào Redis với thời gian hết hạn đã tính toán.
        String redisKey = "event:attendance:" + campaignId + ":user:" + currentUser;
        redisTemplate.opsForValue().set(redisKey, "true", expirationTimeInSeconds);

        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .build();
    }
}
