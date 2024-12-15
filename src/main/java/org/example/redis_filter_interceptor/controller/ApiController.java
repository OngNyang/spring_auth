package org.example.redis_filter_interceptor.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/hello")
    public String   sayHello() {
        return  "Hello, Spring Boot";
    }

    @GetMapping("/perform-action")
    public String   performAction() {
        Authentication  authentication = SecurityContextHolder.getContext().getAuthentication();
        String          username = authentication.getName();
        boolean         isAdmin = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (isAdmin) {
            return ("Admin action performed by " + username);
        } else {
            return ("User action performed by " + username);
        }
    }
}
