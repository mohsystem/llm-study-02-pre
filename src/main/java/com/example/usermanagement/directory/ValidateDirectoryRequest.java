package com.example.usermanagement.directory;

import jakarta.validation.constraints.NotBlank;

public record ValidateDirectoryRequest(@NotBlank String dc, @NotBlank String username) {
}
