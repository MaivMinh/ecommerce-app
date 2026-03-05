package com.minh.support_service.payload.response;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CloudinaryUploadResult extends UploadResult {
    private String publicId;
}
