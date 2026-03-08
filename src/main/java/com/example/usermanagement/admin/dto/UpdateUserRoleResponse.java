package com.example.usermanagement.admin.dto;

import com.example.usermanagement.user.UserRole;

public record UpdateUserRoleResponse(Long id, String publicRef, UserRole role) {
}
