package com.financeapp.service;

import com.financeapp.dto.user.UpdateProfileRequest;
import com.financeapp.dto.user.UserProfileResponse;
import java.util.UUID;

// TODO: view/update profile (fullName, email) - reuse AuthService.changePassword for password changes.
public interface UserProfileService {
    UserProfileResponse getProfile(UUID userId);
    UserProfileResponse updateProfile(UUID userId, UpdateProfileRequest request);
}
