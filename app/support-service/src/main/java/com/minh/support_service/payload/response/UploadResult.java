package com.minh.support_service.payload.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadResult {
    private String data;
    private String provider;
}
