package com.minh.notify_service.controller;

import com.minh.common.response.ResponseData;
import com.minh.notify_service.payload.request.CreateTemplateRequest;
import com.minh.notify_service.payload.request.SearchTemplatesRequest;
import com.minh.notify_service.payload.request.UpdateTemplateRequest;
import com.minh.notify_service.service.TemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(value = "/api/templates")
public class TemplateController {
    private final TemplateService templateService;

    @PostMapping(value = "/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> searchTemplates(@RequestBody SearchTemplatesRequest request) {
        ResponseData response = templateService.searchTemplates(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(value = "/{templateId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> getTemplateByCode(@PathVariable(name = "templateId") String templateId) {
        ResponseData response = templateService.getTemplateById(templateId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(value = "")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> createTemplate(@RequestBody @Valid CreateTemplateRequest request) {
        ResponseData response = templateService.createTemplate(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(value = "")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> updateTemplate(@RequestBody @Valid UpdateTemplateRequest request) {
        ResponseData response = templateService.updateTemplate(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(value = "/{templateId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> deleteTemplate(@PathVariable(name = "templateId") String templateId) {
        ResponseData response = templateService.deleteTemplate(templateId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(value = "/{templateCode}/render-preview")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> renderTemplatePreview(
            @PathVariable(name = "templateCode") String templateCode,
            @RequestBody String variablesJson) {
        ResponseData response = templateService.renderTemplatePreview(templateCode, variablesJson);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}