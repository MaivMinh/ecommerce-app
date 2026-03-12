package com.minh.notify_service.service;

import com.minh.common.functions.input.NotifyOrderCompletedEvent;
import com.minh.common.functions.input.NotifyOrderCancelledEvent;

public interface NotificationService {
    void handleNotifyOrderConfirmed(NotifyOrderCompletedEvent event);

    void handleNotifyOrderCancelled(NotifyOrderCancelledEvent event);
}
