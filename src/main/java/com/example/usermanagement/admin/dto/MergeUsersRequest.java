package com.example.usermanagement.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record MergeUsersRequest(
    @NotBlank String sourcePublicRef,
    @NotBlank String targetPublicRef
) {
}
