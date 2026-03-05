package com.minh.support_service.kafka.consumer;

import com.minh.support_service.service.impl.CloudinaryFileStorage;
import com.minh.support_service.service.impl.MinioFileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class KafkaFileStorageConsumer {
    private final CloudinaryFileStorage cloudinaryFileStorage;
    private final MinioFileStorage minioFileStorage;

    @KafkaListener(
            topics = "file.cloudinary.delete",
            groupId = "support-service"
    )
    public void consumeFileCloudinaryDelete(String identifier) {
        log.info("Received file delete request for identifier: {}", identifier);
        try {
            boolean isDeleted = cloudinaryFileStorage.delete(identifier);
            if (isDeleted) {
                log.info("Successfully deleted file with identifier: {}", identifier);
            } else {
                log.error("Failed to delete file with identifier: {}", identifier);
            }
        }   catch (Exception e) {
            log.error("Error processing file delete request for identifier: {}", identifier, e);
        }
    }
}
