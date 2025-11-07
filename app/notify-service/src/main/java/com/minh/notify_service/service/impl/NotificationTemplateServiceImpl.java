package com.minh.notify_service.service.impl;

import com.minh.notify_service.dto.NotificationTemplateDto;
import com.minh.notify_service.entity.NotificationTemplate;
import com.minh.notify_service.repository.NotificationTemplateRepository;
import com.minh.notify_service.service.NotificationTemplateService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotificationTemplateServiceImpl implements NotificationTemplateService {
    private final NotificationTemplateRepository repository;
    private final ModelMapper modelMapper;

    @Override
    public NotificationTemplateDto findNotificationTemplateByTemplateCodeAndIsActive(String templateCode, boolean b) {
        if (!StringUtils.hasText(templateCode)) {
            return null;
        }
        NotificationTemplate template = repository.findNotificationTemplateByTemplateCodeAndIsActive(templateCode, b);
        if (Objects.isNull(template)) {
            return null;
        }
        return modelMapper.map(template, NotificationTemplateDto.class);
    }
}
