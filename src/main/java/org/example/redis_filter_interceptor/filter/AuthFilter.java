package org.example.redis_filter_interceptor.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class AuthFilter extends OncePerRequestFilter {
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserDetailsService            userDetailsService;
    private static final String                 SESSION_PREFIX = "spring:session:sessions:";

    public AuthFilter(RedisTemplate<String, Object> redisTemplate, UserDetailsService userDetailsService) {
        this.redisTemplate = redisTemplate;
        this.userDetailsService = userDetailsService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token != null) {
            token = SESSION_PREFIX + token;
            if (redisTemplate.hasKey(token)) {
                String                              jsonValue = (String) redisTemplate.opsForValue().get(token);
                ObjectMapper                        objectMapper = new ObjectMapper();
                Map<String, Object>                 userData = objectMapper.readValue(jsonValue, new TypeReference<>() {});
                String                              username = (String) userData.get("username");
                List<String>                        roles = (List<String>) userData.get("roles");
                List<SimpleGrantedAuthority>        authorities = roles.stream().map(SimpleGrantedAuthority::new).toList();
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                System.out.println("Username: " + username + ", Roles: " + roles);
            } else {
                System.out.println("Token is invalid or expired");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token.");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}