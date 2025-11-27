package com.minh.event_service.controller;

import com.minh.common.constants.ResponseMessages;
import com.minh.common.response.ResponseData;
import com.minh.event_service.payload.request.CreateVoucherRequest;
import com.minh.event_service.payload.request.SearchVouchersRequest;
import com.minh.event_service.payload.request.UpdateVoucherRequest;
import com.minh.event_service.service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(value = "/api/vouchers")
public class VoucherController {
    private final VoucherService voucherService;

    @PostMapping(value = "")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> createVoucher(@RequestBody @Valid CreateVoucherRequest request) {
        voucherService.createVoucher(request);

        return ResponseEntity.ok(ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(null)
                .build()
        );
    }

    @PostMapping(value = "/search")
    public ResponseEntity<ResponseData> searchVouchers(@RequestBody SearchVouchersRequest request) {
        ResponseData response = voucherService.searchVouchers(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ResponseData> getVoucherDetailById(@PathVariable("id") String id) {
        ResponseData response = voucherService.getVoucherDetailById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(value = "")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> updateVoucher(@RequestBody @Valid UpdateVoucherRequest request) {
        voucherService.updateVoucher(request);
        return ResponseEntity.ok(ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(null)
                .build()
        );
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> deleteVoucher(@PathVariable("id") String id) {
        voucherService.deleteVoucher(id);
        return ResponseEntity.ok(ResponseData.builder()
                .status(200)
                .message(ResponseMessages.SUCCESS)
                .data(null)
                .build()
        );
    }
}
