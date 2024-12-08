package org.example.redis_filter_interceptor.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class AuthFilter extends OncePerRequestFilter {
    private final RedisTemplate<String, Object> redisTemplate;

    public AuthFilter(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        System.out.println("filter executed.");

        if (token != null) {
            System.out.println("Received Token: " + token);
            if (redisTemplate.hasKey(token)) {
                System.out.println("Token is valid");

                // Redis에서 username 가져오기
                String username = (String) redisTemplate.opsForValue().get(token);
                System.out.println("username: " + username);

                // Spring Security 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null, // 비밀번호는 필요하지 않음
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) // 기본 역할 추가
                        );

                // 인증 객체를 SecurityContext에 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                System.out.println("Token is invalid or expired");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token.");
                return;
            }
        }

        // 토큰이 없으면 요청을 거부하지 않고 다음 필터로 전달
        filterChain.doFilter(request, response);
    }
}