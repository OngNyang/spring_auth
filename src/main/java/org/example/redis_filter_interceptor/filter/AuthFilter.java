package org.example.redis_filter_interceptor.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {
    private final RedisTemplate<String, Object> redisTemplate;

    public AuthFilter(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void  doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String  token = request.getHeader("Authorization");
        String  username = null;

        if (token != null && redisTemplate.hasKey(token)) {
            username = (String) redisTemplate.opsForValue().get(token);
            request.setAttribute("username", username);
        } else if (token != null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token.");
            return ;
        }
        filterChain.doFilter(request, response);
    }

}
