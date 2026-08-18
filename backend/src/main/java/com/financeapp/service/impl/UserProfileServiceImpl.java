package com.financeapp.service.impl;

import com.financeapp.dto.user.UpdateProfileRequest;
import com.financeapp.dto.user.UserProfileResponse;
import com.financeapp.entity.AuditLog;
import com.financeapp.entity.User;
import com.financeapp.exception.ResourceNotFoundException;
import com.financeapp.repository.AuditLogRepository;
import com.financeapp.repository.UserRepository;
import com.financeapp.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    @Override
    public UserProfileResponse getProfile(UUID userId) {
        return toResponse(getUserOrThrow(userId));
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = getUserOrThrow(userId);
        user.setFullName(request.fullName());
        userRepository.save(user);
        auditLogRepository.save(AuditLog.builder().user(user).action("PROFILE_UPDATED").build());
        return toResponse(user);
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    private UserProfileResponse toResponse(User user) {
        return new UserProfileResponse(user.getId(), user.getFullName(), user.getEmail(), user.getCreatedAt());
    }
}
