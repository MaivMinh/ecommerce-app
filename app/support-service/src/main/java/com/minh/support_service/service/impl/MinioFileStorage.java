package com.minh.support_service.service.impl;

import com.minh.support_service.payload.response.UploadResult;
import com.minh.support_service.service.FileStorageService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("MINIO")
@RequiredArgsConstructor
public class MinioFileStorage implements FileStorageService {
    private final MinioClient minioClient;
    private final String MINIO = "MINIO";

    @Value("${minio.bucket}")
    private String bucket;

    @Override
    public UploadResult upload(MultipartFile file) {
        log.info("Uploading file to MinIO");

        try {
            String objectName = UUID.randomUUID() + "-" + file.getOriginalFilename();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(Objects.nonNull(file.getContentType()) ? file.getContentType() : "image/png; image/jpg; image/jpeg")
                            .build()
            );

            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .method(Method.GET)
                            .expiry(3, TimeUnit.HOURS)
                            .build()
            );

            return UploadResult.builder()
                    .data(url)
                    .provider(this.MINIO)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("MinIO upload failed", e);
        }
    }
}
