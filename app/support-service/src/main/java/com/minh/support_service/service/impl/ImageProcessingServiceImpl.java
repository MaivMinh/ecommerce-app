package com.minh.support_service.service.impl;

import com.minh.common.response.ResponseData;
import com.minh.support_service.payload.response.UploadResult;
import com.minh.support_service.service.FileStorageService;
import com.minh.support_service.service.ImageProcessingService;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ImageProcessingServiceImpl implements ImageProcessingService {
    private final Map<String, FileStorageService> storageMap;
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${file.storage.provider}")
    private String provider;

    @Override
    public ResponseData uploadImage(MultipartFile image) {
        FileStorageService storage = storageMap.get(provider);
        UploadResult result = storage.upload(image);

        return ResponseData.builder()
                .status(HttpStatus.CREATED.value())
                .message("Image uploaded successfully")
                .data(result)
                .build();
    }

    @Override
    public InputStream download(String fileName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getImageUrl(String fileName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(fileName)
                            .expiry(3, TimeUnit.HOURS)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}