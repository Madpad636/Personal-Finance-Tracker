package com.financeapp.service;

import com.financeapp.dto.savings.SavingsGoalRequest;
import com.financeapp.dto.savings.SavingsGoalResponse;

import java.util.List;
import java.util.UUID;

// TODO: implement following the IncomeServiceImpl pattern.
public interface SavingsGoalService {
    SavingsGoalResponse create(UUID userId, SavingsGoalRequest request);
    SavingsGoalResponse update(UUID userId, UUID goalId, SavingsGoalRequest request);
    void delete(UUID userId, UUID goalId);
    List<SavingsGoalResponse> list(UUID userId);
}
