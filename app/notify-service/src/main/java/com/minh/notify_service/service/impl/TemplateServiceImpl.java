package com.minh.notify_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.common.constants.ErrorCode;
import com.minh.common.constants.ResponseMessages;
import com.minh.common.message.MessageCommon;
import com.minh.common.response.ResponseData;
import com.minh.common.utils.AppUtils;
import com.minh.notify_service.dto.NotificationTemplateDto;
import com.minh.notify_service.entity.NotificationTemplate;
import com.minh.notify_service.enums.TemplateChannel;
import com.minh.notify_service.payload.request.CreateTemplateRequest;
import com.minh.notify_service.payload.request.SearchTemplatesRequest;
import com.minh.notify_service.payload.request.UpdateTemplateRequest;
import com.minh.notify_service.repository.NotificationTemplateRepository;
import com.minh.notify_service.service.TemplateService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {
    private final NotificationTemplateRepository repository;
    private final MessageCommon messageCommon;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final Configuration freemarkerCfg;

    @Override
    public ResponseData searchTemplates(SearchTemplatesRequest request) {
        if (Objects.isNull(request)) {
            return new ResponseData(400, messageCommon.getMessage(ErrorCode.INVALID_REQUEST), null);
        }

        Pageable pageable = AppUtils.toPageable(request);
        Page<NotificationTemplate> templatePage = repository.searchTemplates(request, pageable);
        Page<NotificationTemplateDto> result = templatePage
                .map(template -> {
                    NotificationTemplateDto dto = new NotificationTemplateDto();
                    modelMapper.map(template, dto);
                    return dto;
                });

        return new ResponseData(200, ResponseMessages.SUCCESS, result);
    }

    @Override
    public ResponseData getTemplateById(String templateId) {
        if (!StringUtils.hasText(templateId)) {
            return ResponseData.builder()
                    .status(404)
                    .message(ResponseMessages.NOT_FOUND)
                    .data(null)
                    .build();
        }

        NotificationTemplate template = repository.findById(templateId).orElse(null);
        if (Objects.isNull(template)) {
            return ResponseData.builder()
                    .status(404)
                    .message(ResponseMessages.NOT_FOUND)
                    .data(null)
                    .build();
        }

        NotificationTemplateDto dto = new NotificationTemplateDto();
        modelMapper.map(template, dto);
        return ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(dto)
                .build();
    }

    @Override
    public ResponseData createTemplate(CreateTemplateRequest request) {
        if (Objects.isNull(request)) {
            return new ResponseData(400, messageCommon.getMessage(ErrorCode.INVALID_REQUEST), null);
        }

        NotificationTemplate saved = repository.findNotificationTemplateByTemplateCodeAndIsActive(request.getTemplateCode(), true);
        if (Objects.nonNull(saved)) {
            return new ResponseData(400, messageCommon.getMessage(ErrorCode.NOTIFICATION_TEMPLATE.TEMPLATE_CODE_EXISTED), null);
        }


        NotificationTemplate template = new NotificationTemplate();
        template.setId(AppUtils.generateUUIDv7());
        template.setTemplateCode(request.getTemplateCode());
        template.setChannel(TemplateChannel.valueOf(request.getChannel()));
        template.setTitle(request.getTitle());
        template.setContent(request.getContent());
        template.setIsActive(Objects.nonNull(request.getIsActive()) ? request.getIsActive() : Boolean.FALSE);

        try {
            if (Objects.nonNull(request.getParams())) {
                template.setParams(objectMapper.writeValueAsString(request.getParams()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Params không hợp lệ", e);
        }

        repository.save(template);
        return new ResponseData(201, ResponseMessages.SUCCESS, null);
    }


    @Override
    public ResponseData updateTemplate(UpdateTemplateRequest request) {
        if (Objects.isNull(request) || !StringUtils.hasText(request.getTemplateCode()) || !StringUtils.hasText(request.getId())) {
            return new ResponseData(400, messageCommon.getMessage(ErrorCode.INVALID_REQUEST), null);
        }
        NotificationTemplate saved = repository.findById(request.getId()).orElse(null);
        if (Objects.isNull(saved)) {
            return new ResponseData(404, messageCommon.getMessage(ErrorCode.NOTIFICATION_TEMPLATE.NOT_FOUND, request.getId()), null);
        }

        saved.setTemplateCode(request.getTemplateCode());
        saved.setChannel(TemplateChannel.valueOf(request.getChannel()));
        saved.setTitle(request.getTitle());
        saved.setContent(request.getContent());
        saved.setIsActive(Objects.nonNull(request.getIsActive()) ? request.getIsActive() : saved.getIsActive());
        try {
            if (Objects.nonNull(request.getParams())) {
                saved.setParams(objectMapper.writeValueAsString(request.getParams()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Params không hợp lệ", e);
        }
        repository.save(saved);
        return new ResponseData(200, ResponseMessages.SUCCESS, null);
    }

    @Override
    public ResponseData deleteTemplate(String templateId) {
        if (!StringUtils.hasText(templateId)) {
            return new ResponseData(400, messageCommon.getMessage(ErrorCode.INVALID_REQUEST), null);
        }
        NotificationTemplate saved = repository.findById(templateId).orElse(null);
        if (Objects.isNull(saved)) {
            return new ResponseData(404, messageCommon.getMessage(ErrorCode.NOTIFICATION_TEMPLATE.NOT_FOUND, templateId), null);
        }
        repository.delete(saved);
        return new ResponseData(200, ResponseMessages.SUCCESS, null);
    }

    @Override
    public ResponseData renderTemplatePreview(String templateCode, String variablesJson) {
        return null;
    }
}
