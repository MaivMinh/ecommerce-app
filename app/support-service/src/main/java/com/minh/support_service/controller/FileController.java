package com.minh.support_service.controller;

import com.minh.common.response.ResponseData;
import com.minh.support_service.service.ImageProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/files")
@Validated
@RequiredArgsConstructor
public class FileController {
    private final ImageProcessingService imageProcessingService;

    @PostMapping(value = "/upload")
    public ResponseEntity<ResponseData> uploadImage(@RequestPart("image")MultipartFile image) {
        ResponseData response = imageProcessingService.uploadImage(image);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable(name = "fileName") String fileName) {
        InputStreamResource resource =
                new InputStreamResource(imageProcessingService.download(fileName));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + fileName)
                .body(resource);

    }

    @GetMapping("/{fileName}/view")
    public ResponseEntity<?> viewImage(@PathVariable(name = "fileName") String fileName) {
        String url = imageProcessingService.getImageUrl(fileName);

        return ResponseEntity.ok(Map.of("url", url));
    }
}
