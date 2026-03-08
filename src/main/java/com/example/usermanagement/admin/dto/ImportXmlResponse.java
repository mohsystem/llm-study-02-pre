package com.example.usermanagement.admin.dto;

import java.util.List;

public record ImportXmlResponse(int imported, int skipped, int rejected, List<String> messages) {
}
