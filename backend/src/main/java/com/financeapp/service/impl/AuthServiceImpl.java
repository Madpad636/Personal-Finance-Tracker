package com.financeapp.service.impl;

import com.financeapp.dto.auth.*;
import com.financeapp.entity.AuditLog;
import com.financeapp.entity.User;
import com.financeapp.exception.AccountLockedException;
import com.financeapp.exception.DuplicateResourceException;
import com.financeapp.exception.InvalidCredentialsException;
import com.financeapp.repository.AuditLogRepository;
import com.financeapp.repository.UserRepository;
import com.financeapp.security.JwtUtil;
import com.financeapp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 15;

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.password()))
                .enabled(true)
                .failedLoginAttempts(0)
                .build();
        user = userRepository.save(user);

        audit(user, "USER_REGISTERED", "New account created", null);

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getEmail());
        return AuthResponse.of(user.getId(), user.getFullName(), user.getEmail(), accessToken, refreshToken);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, String clientIp) {
        User user = userRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> {
                    audit(null, "LOGIN_FAILED", "Unknown email: " + request.email(), clientIp);
                    return new InvalidCredentialsException("Invalid email or password");
                });

        if (user.getAccountLockedUntil() != null && user.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
            audit(user, "LOGIN_BLOCKED_ACCOUNT_LOCKED", null, clientIp);
            throw new AccountLockedException("Account is locked. Try again after " + user.getAccountLockedUntil());
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            registerFailedAttempt(user, clientIp);
            throw new InvalidCredentialsException("Invalid email or password");
        }

        user.setFailedLoginAttempts(0);
        user.setAccountLockedUntil(null);
        userRepository.save(user);

        audit(user, "LOGIN_SUCCESS", null, clientIp);

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getEmail());
        return AuthResponse.of(user.getId(), user.getFullName(), user.getEmail(), accessToken, refreshToken);
    }

    @Override
    public AuthResponse refresh(RefreshTokenRequest request) {
        String token = request.refreshToken();
        if (jwtUtil.isTokenExpired(token)) {
            throw new InvalidCredentialsException("Refresh token expired, please log in again");
        }
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token"));

        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getEmail());
        return AuthResponse.of(user.getId(), user.getFullName(), user.getEmail(), newAccessToken, newRefreshToken);
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        audit(user, "PASSWORD_CHANGED", null, null);
    }

    @Override
    public void logout(UUID userId) {
        // Stateless JWT: logout is enforced client-side by discarding tokens.
        // For server-side revocation, back this with a token-blocklist (e.g. Redis) keyed by JTI.
        userRepository.findById(userId).ifPresent(u -> audit(u, "LOGOUT", null, null));
    }

    private void registerFailedAttempt(User user, String clientIp) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
            audit(user, "ACCOUNT_LOCKED", "Locked after " + attempts + " failed attempts", clientIp);
        } else {
            audit(user, "LOGIN_FAILED", "Attempt " + attempts + " of " + MAX_FAILED_ATTEMPTS, clientIp);
        }
        userRepository.save(user);
    }

    private void audit(User user, String action, String details, String ip) {
        try {
            auditLogRepository.save(AuditLog.builder()
                    .user(user)
                    .action(action)
                    .details(details)
                    .ipAddress(ip)
                    .build());
        } catch (Exception e) {
            log.warn("Failed to write audit log for action {}: {}", action, e.getMessage());
        }
    }
}
