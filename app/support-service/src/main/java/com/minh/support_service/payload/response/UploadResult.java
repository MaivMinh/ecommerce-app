package com.minh.support_service.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UploadResult {
    private String data;
    private String provider;
}
