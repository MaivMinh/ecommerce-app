package com.minh.support_service.service.impl;

import com.minh.common.response.ResponseData;
import com.minh.support_service.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public ResponseData uploadImage(MultipartFile image) {
        return null;
    }
}
