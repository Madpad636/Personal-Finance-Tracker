package com.financeapp.controller;

import com.financeapp.dto.budget.BudgetRequest;
import com.financeapp.dto.budget.BudgetResponse;
import com.financeapp.security.AuthenticatedUser;
import com.financeapp.service.BudgetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// TODO: implement BudgetService.
@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Tag(name = "Budgets", description = "Manage monthly budgets")
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetResponse> create(@AuthenticationPrincipal AuthenticatedUser user,
                                                  @Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.create(user.getId(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> update(@AuthenticationPrincipal AuthenticatedUser user,
                                                  @PathVariable UUID id, @Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.ok(budgetService.update(user.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id) {
        budgetService.delete(user.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> listForMonth(@AuthenticationPrincipal AuthenticatedUser user,
                                                               @RequestParam int month, @RequestParam int year) {
        return ResponseEntity.ok(budgetService.listForMonth(user.getId(), month, year));
    }
}
