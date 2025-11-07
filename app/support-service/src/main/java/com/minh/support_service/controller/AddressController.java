package com.minh.support_service.controller;

import com.minh.common.response.ResponseData;
import com.minh.support_service.DTO.AddressDto;
import com.minh.support_service.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/addresses")
@Validated
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @GetMapping(value = "")
    public ResponseEntity<ResponseData> getAddresses() {
        ResponseData response = addressService.getAddressesByUsername();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(value = "")
    public ResponseEntity<ResponseData> createAddress(@RequestBody AddressDto addressDto) {
        ResponseData response = addressService.createAddress(addressDto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(value = "")
    public ResponseEntity<ResponseData> updateAddress(@RequestBody AddressDto addressDto) {
        ResponseData response = addressService.updateAddress(addressDto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ResponseData> deleteAddress(@PathVariable("id") String id) {
        ResponseData response = addressService.deleteAddress(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PatchMapping(value = "/{id}/set-as-default")
    public ResponseEntity<ResponseData> setDefaultAddress(@PathVariable("id") String id){
        ResponseData response = addressService.setDefaultAddress(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
