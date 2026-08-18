package com.financeapp.controller;

import com.financeapp.dto.expense.ExpenseRequest;
import com.financeapp.dto.expense.ExpenseResponse;
import com.financeapp.security.AuthenticatedUser;
import com.financeapp.service.ExpenseService;
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

// TODO: implement ExpenseService (see IncomeController/IncomeServiceImpl for the reference pattern).
@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@Tag(name = "Expenses", description = "Manage expense records")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseResponse> create(@AuthenticationPrincipal AuthenticatedUser user,
                                                   @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.create(user.getId(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> update(@AuthenticationPrincipal AuthenticatedUser user,
                                                   @PathVariable UUID id, @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(expenseService.update(user.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id) {
        expenseService.delete(user.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getById(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id) {
        return ResponseEntity.ok(expenseService.getById(user.getId(), id));
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> list(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false, defaultValue = "date") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String direction) {
        return ResponseEntity.ok(expenseService.list(user.getId(), from, to, categoryId, sortBy, direction));
    }
}
