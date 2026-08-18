package com.financeapp.controller;

import com.financeapp.dto.income.IncomeRequest;
import com.financeapp.dto.income.IncomeResponse;
import com.financeapp.security.AuthenticatedUser;
import com.financeapp.service.IncomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Reference implementation for a resource module. Expense/Budget/SavingsGoal
 * controllers should follow this same shape: authenticate via the injected
 * AuthenticatedUser principal (never trust a userId from the request body),
 * delegate to the service layer, and return DTOs (never entities).
 */
@RestController
@RequestMapping("/api/incomes")
@RequiredArgsConstructor
@Tag(name = "Income", description = "Manage income records")
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    @Operation(summary = "Add a new income record")
    public ResponseEntity<IncomeResponse> create(@AuthenticationPrincipal AuthenticatedUser user,
                                                  @Valid @RequestBody IncomeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(incomeService.create(user.getId(), request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing income record")
    public ResponseEntity<IncomeResponse> update(@AuthenticationPrincipal AuthenticatedUser user,
                                                  @PathVariable UUID id,
                                                  @Valid @RequestBody IncomeRequest request) {
        return ResponseEntity.ok(incomeService.update(user.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an income record")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id) {
        incomeService.delete(user.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single income record")
    public ResponseEntity<IncomeResponse> getById(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id) {
        return ResponseEntity.ok(incomeService.getById(user.getId(), id));
    }

    @GetMapping
    @Operation(summary = "List income records, optionally filtered by date range and sorted")
    public ResponseEntity<List<IncomeResponse>> list(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false, defaultValue = "date") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String direction) {
        return ResponseEntity.ok(incomeService.list(user.getId(), from, to, sortBy, direction));
    }
}
