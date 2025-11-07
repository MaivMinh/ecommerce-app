package com.minh.support_service.controller;

import com.minh.common.response.ResponseData;
import com.minh.support_service.DTO.UserDto;
import com.minh.support_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(value = "/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping(value = "/profile")
    public ResponseEntity<ResponseData> getProfile() {
        ResponseData response = userService.getProfile();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(value = "/profile")
    public ResponseEntity<ResponseData> updateProfile(@RequestBody UserDto dto) {
        ResponseData response = userService.updateProfile(dto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}