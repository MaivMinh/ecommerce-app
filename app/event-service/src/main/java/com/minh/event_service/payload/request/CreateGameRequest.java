package com.minh.event_service.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGameRequest {
    @NotBlank
    private String name;
}