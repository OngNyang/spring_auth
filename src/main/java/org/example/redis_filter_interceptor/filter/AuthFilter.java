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
    private final UserDetailsService userDetailsService;

    public AuthFilter(RedisTemplate<String, Object> redisTemplate, UserDetailsService userDetailsService) {
        this.redisTemplate = redisTemplate;
        this.userDetailsService = userDetailsService;
    }

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        String token = request.getHeader("Authorization");
//
//        if (token != null) {
//            System.out.println("Received Token: " + token);
//            if (redisTemplate.hasKey(token)) {
//                System.out.println("Token is valid");
//
//                // Redis에서 username 가져오기
//                String username = (String) redisTemplate.opsForValue().get(token);
//                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//                System.out.println("username: " + username);
//
//                // Spring Security 인증 객체 생성
//                UsernamePasswordAuthenticationToken authentication =
//                        new UsernamePasswordAuthenticationToken(
//                                username,
//                                null, // 비밀번호는 필요하지 않음
//                                userDetails.getAuthorities()
////                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) // 기본 역할 추가
//                        );
//
//                // 인증 객체를 SecurityContext에 설정
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            } else {
//                System.out.println("Token is invalid or expired");
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token.");
//                return;
//            }
//        }
//
//        // 토큰이 없으면 요청을 거부하지 않고 다음 필터로 전달
//        filterChain.doFilter(request, response);
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token != null) {
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