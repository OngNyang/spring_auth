package org.example.redis_filter_interceptor.controller;

import org.example.redis_filter_interceptor.exception.InvalidCredentialsException;
import org.example.redis_filter_interceptor.model.LoginRequest;
import org.example.redis_filter_interceptor.model.LoginResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final RedisTemplate<String, Object> redisTemplate;

    public  AuthController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/login")
    public ResponseEntity<?>    login(@RequestBody LoginRequest loginRequest) {
        String              token = null;
        ResponseEntity<?>   response = null;

        try {
            if ("user".equals(loginRequest.getUsername()) && "password".equals(loginRequest.getPassword())) {
                token = UUID.randomUUID().toString();
                redisTemplate.opsForValue().set(token, loginRequest.getUsername(), 1, TimeUnit.HOURS);
                response = ResponseEntity.ok(new LoginResponse(token));
            } else {
                throw new InvalidCredentialsException("Invalid username or password.");
            }
        } catch(InvalidCredentialsException e) {
            response = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch(Exception e) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }

        return (response);
    }
}
