package org.example.redis_filter_interceptor.service;

import org.example.redis_filter_interceptor.model.Role;
import org.example.redis_filter_interceptor.model.User;
import org.example.redis_filter_interceptor.repository.RoleRepository;
import org.example.redis_filter_interceptor.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {
    private final UserRepository    userRepository;
    private final RoleRepository    roleRepository;

    private final String            ROLE_NAME_ADMIN = "ROLE_ADMIN";

    public AdminService (UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public ResponseEntity<?> assignRoleToUser(String username) {
        Optional<User>      userOptional = userRepository.findByUsername(username);
        Optional<Role>      roleOptional = roleRepository.findByName(ROLE_NAME_ADMIN);
        User                user = null;

        if (userOptional.isEmpty()) {
            return (ResponseEntity.badRequest().body("User not found"));
        }
        if (roleOptional.isEmpty()) {
            return (ResponseEntity.badRequest().body("Role not found"));
        }
        user = userOptional.get();
        user.getRoles().add(roleOptional.get());
        userRepository.save(user);

        return (ResponseEntity.ok("Role assigned succesfully"));
    }

}
