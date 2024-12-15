package org.example.redis_filter_interceptor.service;


import org.example.redis_filter_interceptor.model.Role;
import org.example.redis_filter_interceptor.model.User;
import org.example.redis_filter_interceptor.repository.RoleRepository;
import org.example.redis_filter_interceptor.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository        userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleRepository        roleRepository;
    private final String                ROLE_NAME_USER = "ROLE_USER";

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
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

    public boolean  verifyPassword(String rawPassword, String encodedPassword) {
        return (passwordEncoder.matches(rawPassword, encodedPassword));
    }
}
