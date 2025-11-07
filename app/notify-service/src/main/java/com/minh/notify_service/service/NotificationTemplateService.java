package com.minh.notify_service.service;

import com.minh.notify_service.dto.NotificationTemplateDto;

public interface NotificationTemplateService {
    NotificationTemplateDto findNotificationTemplateByTemplateCodeAndIsActive(String templateCode, boolean b);
}
