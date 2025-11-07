package com.minh.notify_service.service;

import com.minh.common.functions.input.NotifyOrderConfirmedEvent;

public interface NotificationService {
    void handleNotifyOrderConfirmed(NotifyOrderConfirmedEvent event);
}
