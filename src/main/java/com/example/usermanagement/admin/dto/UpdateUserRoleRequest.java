package com.example.usermanagement.admin.dto;

import com.example.usermanagement.user.UserRole;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequest(@NotNull UserRole role) {
}
