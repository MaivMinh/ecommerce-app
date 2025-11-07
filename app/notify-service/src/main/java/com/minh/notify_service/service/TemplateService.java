package com.minh.notify_service.service;

import com.minh.common.response.ResponseData;
import com.minh.notify_service.payload.request.CreateTemplateRequest;
import com.minh.notify_service.payload.request.SearchTemplatesRequest;
import com.minh.notify_service.payload.request.UpdateTemplateRequest;
import jakarta.validation.Valid;

public interface TemplateService {
    ResponseData searchTemplates(SearchTemplatesRequest request);

    ResponseData getTemplateById(String templateCode);

    ResponseData createTemplate(@Valid CreateTemplateRequest request);

    ResponseData updateTemplate(@Valid UpdateTemplateRequest request);

    ResponseData deleteTemplate(String templateId);

    /**
     *Hàm thực hiện render preview template với template code và các tham số truyền vào.
     * @param templateCode: Template code cần render preview
     * @param variablesJson: Các tham số truyền vào dưới dạng JSON.
     * @return: ResponseData chứa nội dung cuối cùng đã được render.
     */
    ResponseData renderTemplatePreview(String templateCode, String variablesJson);
}
