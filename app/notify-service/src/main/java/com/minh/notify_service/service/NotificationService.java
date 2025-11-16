package com.minh.notify_service.service;

import com.minh.common.functions.input.NotifyOrderConfirmedEvent;
import com.minh.common.functions.input.NotifyOrderRolledBackEvent;

public interface NotificationService {
    void handleNotifyOrderConfirmed(NotifyOrderConfirmedEvent event);

    void handleNotifyOrderRolledBack(NotifyOrderRolledBackEvent event);
}
