package com.minh.support_service.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.minh.support_service.payload.response.CloudinaryUploadResult;
import com.minh.support_service.payload.response.UploadResult;
import com.minh.support_service.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@Service("CLOUDINARY")
@RequiredArgsConstructor
public class CloudinaryFileStorage implements FileStorageService {
    private final Cloudinary cloudinary;
    private final String CLOUDINARY = "CLOUDINARY";

    public UploadResult upload(MultipartFile image) {
        log.info("Uploading image to Cloudinary");

        try {
            Map uploadResult = cloudinary.uploader().upload(image.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));
            String secureUrl = (String) uploadResult.get("secure_url");
            return UploadResult.builder()
                    .data(secureUrl)
                    .provider(this.CLOUDINARY)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Image upload failed", e);
        }
    }
}
