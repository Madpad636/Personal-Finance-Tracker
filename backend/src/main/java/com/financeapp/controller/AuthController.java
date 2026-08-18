package com.financeapp.controller;

import com.financeapp.dto.auth.*;
import com.financeapp.security.AuthenticatedUser;
import com.financeapp.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login, token refresh, logout, password change")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user account")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate and receive access/refresh tokens")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.login(request, clientIp(httpRequest)));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Exchange a refresh token for a new token pair")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Log out the current user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal AuthenticatedUser principal) {
        authService.logout(principal.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change the current user's password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal AuthenticatedUser principal,
                                                @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(principal.getId(), request);
        return ResponseEntity.noContent().build();
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return (forwarded != null && !forwarded.isBlank()) ? forwarded.split(",")[0].trim() : request.getRemoteAddr();
    }
}
