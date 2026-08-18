package com.financeapp.service.impl;

import com.financeapp.dto.income.IncomeRequest;
import com.financeapp.dto.income.IncomeResponse;
import com.financeapp.entity.AuditLog;
import com.financeapp.entity.Income;
import com.financeapp.entity.User;
import com.financeapp.exception.ResourceNotFoundException;
import com.financeapp.exception.UnauthorizedAccessException;
import com.financeapp.repository.AuditLogRepository;
import com.financeapp.repository.IncomeRepository;
import com.financeapp.repository.UserRepository;
import com.financeapp.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public IncomeResponse create(UUID userId, IncomeRequest request) {
        User user = userRepository.getReferenceById(userId);
        Income income = Income.builder()
                .user(user)
                .amount(request.amount())
                .source(request.source())
                .date(request.date())
                .description(request.description())
                .build();
        income = incomeRepository.save(income);
        audit(userId, "INCOME_CREATED", income.getId().toString());
        return toResponse(income);
    }

    @Override
    @Transactional
    public IncomeResponse update(UUID userId, UUID incomeId, IncomeRequest request) {
        Income income = getOwnedOrThrow(userId, incomeId);
        income.setAmount(request.amount());
        income.setSource(request.source());
        income.setDate(request.date());
        income.setDescription(request.description());
        audit(userId, "INCOME_UPDATED", incomeId.toString());
        return toResponse(income);
    }

    @Override
    @Transactional
    public void delete(UUID userId, UUID incomeId) {
        Income income = getOwnedOrThrow(userId, incomeId);
        incomeRepository.delete(income);
        audit(userId, "INCOME_DELETED", incomeId.toString());
    }

    @Override
    public IncomeResponse getById(UUID userId, UUID incomeId) {
        return toResponse(getOwnedOrThrow(userId, incomeId));
    }

    @Override
    public List<IncomeResponse> list(UUID userId, LocalDate from, LocalDate to, String sortBy, String direction) {
        List<Income> incomes = (from != null && to != null)
                ? incomeRepository.findByUserIdAndDateBetween(userId, from, to)
                : incomeRepository.findByUserId(userId);

        Comparator<Income> comparator = switch (sortBy == null ? "date" : sortBy) {
            case "amount" -> Comparator.comparing(Income::getAmount);
            default -> Comparator.comparing(Income::getDate);
        };
        if ("desc".equalsIgnoreCase(direction)) comparator = comparator.reversed();

        return incomes.stream().sorted(comparator).map(this::toResponse).toList();
    }

    private Income getOwnedOrThrow(UUID userId, UUID incomeId) {
        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new ResourceNotFoundException("Income not found: " + incomeId));
        if (!income.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("This income record does not belong to the current user");
        }
        return income;
    }

    private void audit(UUID userId, String action, String details) {
        auditLogRepository.save(AuditLog.builder()
                .user(userRepository.getReferenceById(userId))
                .action(action)
                .details(details)
                .build());
    }

    private IncomeResponse toResponse(Income i) {
        return new IncomeResponse(i.getId(), i.getAmount(), i.getSource(), i.getDate(), i.getDescription());
    }
}
