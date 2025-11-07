package com.minh.support_service.service;

import com.minh.common.response.ResponseData;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    ResponseData uploadImage(MultipartFile image);
}
