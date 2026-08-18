package com.financeapp.dto.user;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String fullName,
        String email,
        LocalDateTime createdAt
) {}
