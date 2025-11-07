package com.minh.support_service.DTO;

import com.minh.support_service.enums.Gender;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserDto {
    private String id;
    private String username;
    private String fullName;
    private String avatar;
    private Gender gender;
    private Date birthDate;
    private String emails;
    private String mobiles;
}
