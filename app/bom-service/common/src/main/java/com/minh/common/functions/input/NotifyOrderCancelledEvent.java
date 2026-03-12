package com.minh.common.functions.input;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotifyOrderCancelledEvent extends NotifyEvent {
    private Double total;
    private String orderId;
    private List<OrderedItem> items;

    public void setTemplateCode(String templateCode) {
        super.setTemplateCode(templateCode);
    }
    public void setRecipient(Map<String, String> recipient) {
        super.setRecipient(recipient);
    }
    public void setMetaData(Map<String, Object> metaData) {
        super.setMetaData(metaData);
    }
}
