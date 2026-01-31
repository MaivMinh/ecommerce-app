package com.minh.support_service.service;

import com.minh.support_service.payload.response.UploadResult;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    UploadResult upload(MultipartFile file);
}
