package com.financeapp.controller;

import com.financeapp.dto.category.CategoryResponse;
import com.financeapp.repository.CategoryRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Read-only: categories are seeded (V2__seed_categories.sql), not user-managed in this version. */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Expense categories (seeded defaults)")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> list() {
        List<CategoryResponse> categories = categoryRepository.findAll().stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName(), c.getIcon()))
                .toList();
        return ResponseEntity.ok(categories);
    }
}
