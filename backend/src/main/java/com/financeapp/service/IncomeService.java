package com.financeapp.service;

import com.financeapp.dto.income.IncomeRequest;
import com.financeapp.dto.income.IncomeResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IncomeService {
    IncomeResponse create(UUID userId, IncomeRequest request);
    IncomeResponse update(UUID userId, UUID incomeId, IncomeRequest request);
    void delete(UUID userId, UUID incomeId);
    IncomeResponse getById(UUID userId, UUID incomeId);
    List<IncomeResponse> list(UUID userId, LocalDate from, LocalDate to, String sortBy, String direction);
}
