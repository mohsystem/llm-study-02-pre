package com.example.usermanagement.user.dto;

import com.example.usermanagement.user.AccountStatus;
import com.example.usermanagement.user.User;
import com.example.usermanagement.user.UserRole;
import java.time.LocalDateTime;

public record UserProfileResponse(
    Long id,
    String publicRef,
    String firstName,
    String lastName,
    String email,
    UserRole role,
    AccountStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
            user.getId(),
            user.getPublicRef(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getRole(),
            user.getStatus(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
