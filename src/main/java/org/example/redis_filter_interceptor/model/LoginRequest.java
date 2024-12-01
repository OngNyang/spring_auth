package org.example.redis_filter_interceptor.model;

import lombok.Data;

@Data
public class LoginRequest {
    private String  username;
    private String  password;
}
