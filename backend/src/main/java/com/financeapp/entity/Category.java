package com.financeapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Expense category. Seeded with defaults (Food, Transport, Shopping, ...)
 * but modeled as a table (not an enum) so users could extend it later.
 */
@Entity
@Table(name = "categories", uniqueConstraints = {
        @UniqueConstraint(name = "uk_categories_name", columnNames = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 30)
    private String icon;

    @Builder.Default
    private Boolean isDefault = true;
}
