package com.example.usermanagement.directory;

public record DirectoryUserResponse(
    String query,
    String username,
    String displayName,
    String email,
    String status
) {
}
