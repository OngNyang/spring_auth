package org.example.redis_filter_interceptor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long    id;
    @Column(nullable = false, unique = true)
    private String  username;
    @Column(nullable = false)
    private String  password;
    @Column(unique = true)
    private String  email;
    private String  role = "ROLE_USER";

}
