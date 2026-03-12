package com.minh.order_service.controller;

import com.minh.common.response.ResponseData;
import com.minh.order_service.payload.request.CreatePromotionRequest;
import com.minh.order_service.payload.request.GetPromotionsRequest;
import com.minh.order_service.payload.request.SearchPromotionsRequest;
import com.minh.order_service.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/promotions")
@Validated
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping(value = "")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> createPromotion(@RequestBody @Valid CreatePromotionRequest request) {
        promotionService.createPromotion(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "")
    public ResponseEntity<ResponseData> getPromotions(@RequestParam(value = "page", defaultValue = "0") int page,
                                                      @RequestParam(value = "size", defaultValue = "10") int size) {

        GetPromotionsRequest request = GetPromotionsRequest.builder()
                .page(page)
                .size(size)
                .build();

        ResponseData response = promotionService.getPromotions(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(value = "/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> searchPromotions(@RequestBody @Valid SearchPromotionsRequest request) {
        ResponseData response = promotionService.searchPromotions(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
