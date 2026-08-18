package com.financeapp.security;

import com.financeapp.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Adapts our User entity to Spring Security's UserDetails, and carries the
 * user's UUID so controllers/services can enforce row-level ownership checks.
 */
public class AuthenticatedUser implements UserDetails {

    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final boolean enabled;

    private AuthenticatedUser(UUID id, String email, String passwordHash, boolean enabled) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.enabled = enabled;
    }

    public static AuthenticatedUser from(User user) {
        return new AuthenticatedUser(user.getId(), user.getEmail(), user.getPasswordHash(), user.getEnabled());
    }

    public UUID getId() { return id; }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
    @Override public String getPassword() { return passwordHash; }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return enabled; }
}
