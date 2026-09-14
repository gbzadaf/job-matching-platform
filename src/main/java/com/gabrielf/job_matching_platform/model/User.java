package com.gabrielf.job_matching_platform.model;

import com.gabrielf.job_matching_platform.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 120)
    private Role role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createAt;


    @PrePersist
    protected void onCreate() {
        this.createAt = LocalDateTime.now();
    }
}
