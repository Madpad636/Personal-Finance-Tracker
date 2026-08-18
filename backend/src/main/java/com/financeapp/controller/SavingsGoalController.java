package com.financeapp.controller;

import com.financeapp.dto.savings.SavingsGoalRequest;
import com.financeapp.dto.savings.SavingsGoalResponse;
import com.financeapp.security.AuthenticatedUser;
import com.financeapp.service.SavingsGoalService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// TODO: implement SavingsGoalService.
@RestController
@RequestMapping("/api/savings-goals")
@RequiredArgsConstructor
@Tag(name = "Savings Goals", description = "Manage savings goals")
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    @PostMapping
    public ResponseEntity<SavingsGoalResponse> create(@AuthenticationPrincipal AuthenticatedUser user,
                                                        @Valid @RequestBody SavingsGoalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(savingsGoalService.create(user.getId(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavingsGoalResponse> update(@AuthenticationPrincipal AuthenticatedUser user,
                                                        @PathVariable UUID id, @Valid @RequestBody SavingsGoalRequest request) {
        return ResponseEntity.ok(savingsGoalService.update(user.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id) {
        savingsGoalService.delete(user.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SavingsGoalResponse>> list(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(savingsGoalService.list(user.getId()));
    }
}
