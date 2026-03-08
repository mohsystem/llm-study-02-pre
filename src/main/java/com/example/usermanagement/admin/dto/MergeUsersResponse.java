package com.example.usermanagement.admin.dto;

public record MergeUsersResponse(
    String sourcePublicRef,
    String targetPublicRef,
    String result,
    String details
) {
}
