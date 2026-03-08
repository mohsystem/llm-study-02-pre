package com.example.usermanagement.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(
    @Size(max = 100) String firstName,
    @Size(max = 100) String lastName,
    @Email @Size(max = 255) String email
) {
}
