package com.gabrielf.job_matching_platform.dto.response;

import com.gabrielf.job_matching_platform.model.User;
import com.gabrielf.job_matching_platform.model.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        Role role,
        LocalDateTime createdAt

) {
    public static UserResponse fromEntity (User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
