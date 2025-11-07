package com.minh.support_service.controller;

import com.minh.common.response.ResponseData;
import com.minh.support_service.service.ImageProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/files")
@Validated
@RequiredArgsConstructor
public class FileController {
    private final ImageProcessingService imageProcessingService;

    @PostMapping(value = "/images/upload")
    public ResponseEntity<ResponseData> uploadImage(@RequestPart("image")MultipartFile image) {
        ResponseData response = imageProcessingService.uploadImage(image);
        return ResponseEntity.ok(response);
    }
}
