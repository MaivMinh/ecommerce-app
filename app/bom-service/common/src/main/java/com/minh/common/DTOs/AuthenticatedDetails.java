package com.minh.common.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticatedDetails {
    private String givenName;
    private String familyName;
    private String email;
}
