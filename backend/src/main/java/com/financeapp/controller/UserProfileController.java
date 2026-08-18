package com.financeapp.controller;

import com.financeapp.dto.user.UpdateProfileRequest;
import com.financeapp.dto.user.UserProfileResponse;
import com.financeapp.security.AuthenticatedUser;
import com.financeapp.service.UserProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

// TODO: implement UserProfileService.
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "View and update the current user's profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(userProfileService.getProfile(user.getId()));
    }

    @PutMapping
    public ResponseEntity<UserProfileResponse> updateProfile(@AuthenticationPrincipal AuthenticatedUser user,
                                                               @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userProfileService.updateProfile(user.getId(), request));
    }
}
