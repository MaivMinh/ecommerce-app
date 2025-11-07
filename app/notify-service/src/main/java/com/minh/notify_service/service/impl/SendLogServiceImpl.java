package com.minh.notify_service.service.impl;

import com.minh.common.constants.ResponseMessages;
import com.minh.common.response.ResponseData;
import com.minh.common.utils.AppUtils;
import com.minh.notify_service.dto.NotificationSendLogDto;
import com.minh.notify_service.entity.NotificationSendLog;
import com.minh.notify_service.payload.request.SearchSendLogsRequest;
import com.minh.notify_service.repository.NotificationSendLogRepository;
import com.minh.notify_service.service.SendLogService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SendLogServiceImpl implements SendLogService {
    private final NotificationSendLogRepository repository;
    private final ModelMapper modelMapper;

    @Override
    public ResponseData getSendLogById(String logId) {
        NotificationSendLog sendLog = repository.findById(logId).orElse(null);
        if (Objects.isNull(sendLog)) {
            return ResponseData.builder()
                    .status(404)
                    .message("Không tìm thấy bản ghi đã gửi.")
                    .data(null)
                    .build();
        }

        NotificationSendLogDto dto = new NotificationSendLogDto();
        modelMapper.map(sendLog,dto);
        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(dto)
                .build();
    }

    @Override
    public ResponseData searchSendLogs(SearchSendLogsRequest request) {
        if (Objects.isNull(request)) {
            return new ResponseData(400, ResponseMessages.BAD_REQUEST, null);
        }

        Pageable pageable = AppUtils.toPageable(request);
        Page<NotificationSendLog> sendLogPage = repository.searchSendLogs(request, pageable);
        Page<NotificationSendLogDto> result = sendLogPage
                .map(sendLog -> {
                    NotificationSendLogDto dto = new NotificationSendLogDto();
                    modelMapper.map(sendLog, dto);
                    return dto;
                });

        return new ResponseData(200, ResponseMessages.SUCCESS, result);
    }
}
