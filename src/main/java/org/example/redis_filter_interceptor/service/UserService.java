package org.example.redis_filter_interceptor.service;


import org.example.redis_filter_interceptor.model.User;
import org.example.redis_filter_interceptor.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository        userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String username, String password, String email) {
        String  encodedPassword = null;
        User    user = null;

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        encodedPassword = passwordEncoder.encode(password);
        user = User.builder()
                .username(username)
                .password(encodedPassword)
                .email(email)
                .build();

        return (userRepository.save(user));
    }

    public Optional<User>   findByUsername(String username) {
        return (userRepository.findByUsername(username));
    }

    public boolean  verifyPassword(String rawPassword, String encodedPassword) {
        return (passwordEncoder.matches(rawPassword, encodedPassword));
    }
}
