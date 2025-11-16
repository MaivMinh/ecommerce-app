package com.minh.common.functions.input;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotifyOrderConfirmedEvent extends NotifyEvent {
    private NotifyOrderConfirmedParams params;
}