package org.example.redis_filter_interceptor.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long    id;

    @Column(nullable = false, unique = true)
    private String  name;
}
