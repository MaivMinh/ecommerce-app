package com.minh.support_service.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto {
    private String id;
    private String username;
    private String fullName;
    private String phone;
    private String address;
    private Boolean isDefault;
}
