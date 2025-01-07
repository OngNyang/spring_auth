package org.example.redis_filter_interceptor.repository;

import org.example.redis_filter_interceptor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User>  findByUsername(String username);
}
