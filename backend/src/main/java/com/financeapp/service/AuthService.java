package com.financeapp.service;

import com.financeapp.dto.auth.*;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request, String clientIp);
    AuthResponse refresh(RefreshTokenRequest request);
    void changePassword(java.util.UUID userId, ChangePasswordRequest request);
    void logout(java.util.UUID userId);
}
