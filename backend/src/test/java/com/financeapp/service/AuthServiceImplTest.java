package com.financeapp.service;

import com.financeapp.dto.auth.LoginRequest;
import com.financeapp.dto.auth.RegisterRequest;
import com.financeapp.entity.User;
import com.financeapp.exception.DuplicateResourceException;
import com.financeapp.exception.InvalidCredentialsException;
import com.financeapp.repository.AuditLogRepository;
import com.financeapp.repository.UserRepository;
import com.financeapp.security.JwtUtil;
import com.financeapp.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthServiceImpl. This is the reference test for the pattern
 * the rest of the service layer's unit tests should follow: mock repositories,
 * assert on both the returned DTO and the side effects (saves, audit logs).
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private AuditLogRepository auditLogRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("Jane Doe", "jane@example.com", "Str0ng!Pass");
    }

    @Test
    void register_createsUser_whenEmailNotTaken() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });
        when(jwtUtil.generateAccessToken(any(), anyString())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(any(), anyString())).thenReturn("refresh-token");

        var response = authService.register(registerRequest);

        assertThat(response.email()).isEqualTo("jane@example.com");
        assertThat(response.accessToken()).isEqualTo("access-token");
        verify(userRepository).save(any(User.class));
        verify(auditLogRepository).save(any());
    }

    @Test
    void register_throwsDuplicateResourceException_whenEmailAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_throwsInvalidCredentials_whenEmailNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest("nobody@example.com", "whatever");

        assertThatThrownBy(() -> authService.login(request, "127.0.0.1"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_throwsInvalidCredentials_whenPasswordDoesNotMatch() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("jane@example.com")
                .passwordHash("hashed")
                .failedLoginAttempts(0)
                .build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        LoginRequest request = new LoginRequest("jane@example.com", "wrong-password");

        assertThatThrownBy(() -> authService.login(request, "127.0.0.1"))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(userRepository).save(user); // failed-attempt counter persisted
    }
}
