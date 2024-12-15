package org.example.redis_filter_interceptor.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.redis_filter_interceptor.dto.LoginRequestDto;
import org.example.redis_filter_interceptor.model.Role;
import org.example.redis_filter_interceptor.model.User;
import org.example.redis_filter_interceptor.repository.RoleRepository;
import org.example.redis_filter_interceptor.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    private final UserRepository                userRepository;
    private final BCryptPasswordEncoder         passwordEncoder;
    private final RoleRepository                roleRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final String                        ROLE_NAME_USER = "ROLE_USER";

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, RoleRepository roleRepository, RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.redisTemplate = redisTemplate;
    }

    public User registerUser(String username, String password, String email) {
        String  encodedPassword = null;
        User    user = null;

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        encodedPassword = passwordEncoder.encode(password);
        //롤 넣어주기.
        user = User.builder()
                .username(username)
                .password(encodedPassword)
                .email(email)
                .roles(getDefaultUserRole())
                .build();

        return (userRepository.save(user));
    }

    public Set<Role>    getDefaultUserRole() {
        Optional<Role>  roleOptional = roleRepository.findByName(ROLE_NAME_USER);
        Set<Role>       userRoles = new HashSet<>();

        if (roleOptional.isEmpty()) {
            throw new RuntimeException("There is no role named as : ROLE_USER");
        }
        userRoles.add(roleOptional.get());

        return (userRoles);
    }

    public Optional<User>   findByUsername(String username) {
        return (userRepository.findByUsername(username));
    }

//    public boolean  verifyPassword(String rawPassword, String encodedPassword) {
//        return (passwordEncoder.matches(rawPassword, encodedPassword));
//    }

    public User verifyUser(LoginRequestDto loginRequestDto) {
        Optional<User>  userOptional = userRepository.findByUsername(loginRequestDto.getUsername());
        User            user = null;

        if (userOptional.isEmpty() || !passwordEncoder.matches(loginRequestDto.getPassword(), userOptional.get().getPassword())) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        user = userOptional.get();

        return (user);
    }

    public  String  createToken(User user) throws Exception{
        Map<String, Object> userData = new HashMap<>();
        ObjectMapper        objectMapper = new ObjectMapper();
        String              jsonValue = null;
        String              token = null;

        userData.put("username", user.getUsername());
        userData.put("roles", user.getRoles().stream().map(Role::getName).toList());
        jsonValue = objectMapper.writeValueAsString(userData);
        token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(token,jsonValue,1, TimeUnit.HOURS);

        return (token);
    }


}
