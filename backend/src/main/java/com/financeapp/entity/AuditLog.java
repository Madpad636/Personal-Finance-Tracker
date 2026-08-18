package com.financeapp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Immutable audit trail of security-relevant and data-mutating events
 * (login attempts, transaction create/update/delete, etc).
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_logs_user", columnList = "user_id"),
        @Index(name = "idx_audit_logs_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // nullable: e.g. failed login with unknown email

    @Column(nullable = false, length = 80)
    private String action; // e.g. LOGIN_SUCCESS, LOGIN_FAILED, EXPENSE_CREATED

    @Column(length = 1000)
    private String details;

    @Column(length = 45)
    private String ipAddress;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
