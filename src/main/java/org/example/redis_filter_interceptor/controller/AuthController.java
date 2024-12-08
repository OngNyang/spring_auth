package org.example.redis_filter_interceptor.controller;

import org.example.redis_filter_interceptor.dto.UserRegistrationRequestDto;
import org.example.redis_filter_interceptor.exception.InvalidCredentialsException;
import org.example.redis_filter_interceptor.dto.LoginRequestDto;
import org.example.redis_filter_interceptor.model.LoginResponse;
import org.example.redis_filter_interceptor.model.User;
import org.example.redis_filter_interceptor.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserService                   userService;

    public  AuthController(RedisTemplate<String, Object> redisTemplate, UserService userService) {
        this.redisTemplate = redisTemplate;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?>    registerUser(@RequestBody UserRegistrationRequestDto requestDto) {
        User    user = null;

        try {
            user = userService.registerUser(requestDto.getUsername(), requestDto.getPassword(), requestDto.getEmail());
            return (ResponseEntity.ok("User registered successfully"));
        } catch (IllegalArgumentException e) {
            return (ResponseEntity.badRequest().body(e.getMessage()));
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?>    login(@RequestBody LoginRequestDto loginRequestDto) {
        String              token = null;
        ResponseEntity<?>   response = null;

        System.out.println("login method called.");

        try {
            Optional<User>  userOptional = userService.findByUsername(loginRequestDto.getUsername());
            User            user = null;

            if (userOptional.isEmpty()) {
                throw new IllegalArgumentException("Invalid username.");
            }
            user = userOptional.get();
            System.out.println(loginRequestDto.getPassword());
            System.out.println(user.getPassword());
            if (!userService.verifyPassword(loginRequestDto.getPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Invalid password.");
            }
            token = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(token, user.getUsername(), 1, TimeUnit.HOURS);
//            if ("user".equals(loginRequest.getUsername()) && "password".equals(loginRequest.getPassword())) {
//                token = UUID.randomUUID().toString();
//                redisTemplate.opsForValue().set(token, loginRequest.getUsername(), 1, TimeUnit.HOURS);
//                response = ResponseEntity.ok(new LoginResponse(token));
//            } else {
//                throw new InvalidCredentialsException("Invalid username or password.");
//            }
            response = ResponseEntity.ok(new LoginResponse(token));
        } catch(InvalidCredentialsException e) {
            response = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch(Exception e) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred." + e.getMessage());
        }

        return (response);
    }
}
