package org.example.redis_filter_interceptor.config;

import org.example.redis_filter_interceptor.filter.AuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain  securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth ->auth
                        .requestMatchers("/auth/login").permitAll()
//                        .requestMatchers("/admin/**").hasRole("ADMIN") // ADMIN만 접근 가능
//                        .requestMatchers("/user/**").hasRole("USER")  // USER만 접근 가능
                        .anyRequest().authenticated()
                );

        return (http.build());
    }

//    @Override
//    protected void  configure(HttpSecurity http) throws Exception {
//        http.csrf().disable()
//                .authorizeRequests()
//                .antMatchers("/auth/login").permitAll()
//                .anyRequest().authenticated()
//                        .and()
//                        .addFilterBeofore(authFilter, UsernamePasswordAuthenticationFilter.class);
//    }
}
