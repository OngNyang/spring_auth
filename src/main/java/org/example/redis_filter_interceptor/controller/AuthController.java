package org.example.redis_filter_interceptor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.redis_filter_interceptor.dto.UserRegistrationRequestDto;
import org.example.redis_filter_interceptor.exception.InvalidCredentialsException;
import org.example.redis_filter_interceptor.dto.LoginRequestDto;
import org.example.redis_filter_interceptor.dto.LoginResponseDto;
import org.example.redis_filter_interceptor.model.Role;
import org.example.redis_filter_interceptor.model.User;
import org.example.redis_filter_interceptor.repository.UserRepository;
import org.example.redis_filter_interceptor.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserService                   userService;
    private final UserRepository userRepository;

    public  AuthController(RedisTemplate<String, Object> redisTemplate, UserService userService, UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        this.userService = userService;
        this.userRepository = userRepository;
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

//    @PostMapping("/login")
//    public ResponseEntity<?>    login(@RequestBody LoginRequestDto loginRequestDto) {
//        String              token = null;
//        ResponseEntity<?>   response = null;
//
//        try {
//            Optional<User>  userOptional = userService.findByUsername(loginRequestDto.getUsername());
//            User            user = null;
//
//            if (userOptional.isEmpty()) {
//                throw new IllegalArgumentException("Invalid username.");
//            }
//            user = userOptional.get();
//            if (!userService.verifyPassword(loginRequestDto.getPassword(), user.getPassword())) {
//                throw new IllegalArgumentException("Invalid password.");
//            }
//            token = UUID.randomUUID().toString();
//            redisTemplate.opsForValue().set(token, user.getUsername(), 1, TimeUnit.HOURS);
//            response = ResponseEntity.ok(new LoginResponseDto(token));
//        } catch(InvalidCredentialsException e) {
//            response = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
//        } catch(Exception e) {
//            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred." + e.getMessage());
//        }
//
//        return (response);
//    }

    @PostMapping("/login")
    public ResponseEntity<?>    login(@RequestBody LoginRequestDto loginRequestDto) {
        User    user = null;
        String  token = null;

        try {
            user = userService.verifyUser(loginRequestDto);
            token = userService.createToken(user);

            return (ResponseEntity.ok(new LoginResponseDto(token)));
        } catch (IllegalArgumentException e) {
            return (ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred." + e.getMessage());
        }
    }

}
