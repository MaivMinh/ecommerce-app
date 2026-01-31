package com.minh.support_service.service;

import com.minh.common.response.ResponseData;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ImageProcessingService {
    ResponseData uploadImage(MultipartFile image);

    InputStream download(String fileName);

    String getImageUrl(String fileName);
}
