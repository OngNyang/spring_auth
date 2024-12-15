package org.example.redis_filter_interceptor.controller;

import org.example.redis_filter_interceptor.repository.RoleRepository;
import org.example.redis_filter_interceptor.repository.UserRepository;
import org.example.redis_filter_interceptor.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService  adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/assign-role/admin")
    public ResponseEntity<?> assignRoleToUser(@RequestParam String username) {

        return (adminService.assignRoleToUser(username));
    }
}
