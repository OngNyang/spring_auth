package org.example.redis_filter_interceptor.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String  username;
    private String  password;
}
