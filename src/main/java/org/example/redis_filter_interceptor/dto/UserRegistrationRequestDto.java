package org.example.redis_filter_interceptor.dto;

import lombok.Data;

@Data
public class UserRegistrationRequestDto {
    private String  username;
    private String  password;
    private String  email;
}
