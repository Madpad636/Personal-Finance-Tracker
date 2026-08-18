package com.financeapp.dto.auth;

import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String fullName,
        String email,
        String accessToken,
        String refreshToken,
        String tokenType
) {
    public static AuthResponse of(UUID userId, String fullName, String email, String accessToken, String refreshToken) {
        return new AuthResponse(userId, fullName, email, accessToken, refreshToken, "Bearer");
    }
}
