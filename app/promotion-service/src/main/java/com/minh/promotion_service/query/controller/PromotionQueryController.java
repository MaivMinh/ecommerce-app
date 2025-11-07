package com.minh.promotion_service.query.controller;

import com.minh.promotion_service.payload.response.ResponseData;
import com.minh.promotion_service.query.queries.GetPromotionsQuery;
import com.minh.promotion_service.query.queries.SearchPromotionsQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@Validated
@RequestMapping(value = "/api/promotions")
@RequiredArgsConstructor
public class
PromotionQueryController {
    private final QueryGateway queryGateway;

    @GetMapping(value = "")
    public ResponseEntity<ResponseData> getPromotions(@RequestParam(value = "page", defaultValue = "0") int page,
                                                      @RequestParam(value = "size", defaultValue = "10") int size) {

        GetPromotionsQuery query = GetPromotionsQuery.builder()
                .page(page)
                .size(size)
                .build();

        ResponseData response = queryGateway.query(query, ResponseTypes.instanceOf(ResponseData.class)).join();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(value = "/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> searchPromotions(@RequestBody @Valid SearchPromotionsQuery query) {
        ResponseData response = queryGateway.query(query, ResponseTypes.instanceOf(ResponseData.class)).join();
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
