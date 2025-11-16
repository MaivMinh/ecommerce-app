package com.minh.common.functions.input;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotifyOrderRolledBackEvent extends NotifyEvent {
    private NotifyOrderRolledBackParams params;

    public void setTemplateCode(String templateCode) {
        super.setTemplateCode(templateCode);
    }
    public void setRecipient(Map<String, String> recipient) {
        super.setRecipient(recipient);
    }
}
