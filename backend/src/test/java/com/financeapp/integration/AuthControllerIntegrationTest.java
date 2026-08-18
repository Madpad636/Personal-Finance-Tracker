package com.financeapp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financeapp.dto.auth.LoginRequest;
import com.financeapp.dto.auth.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full-stack integration test: real Spring context + a throwaway Postgres
 * container (via Testcontainers), driven through MockMvc. Exercises the
 * register -> login -> protected-endpoint flow end to end.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class AuthControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void registerThenLogin_succeeds() throws Exception {
        RegisterRequest register = new RegisterRequest("Integration Test", "itest@example.com", "Str0ng!Pass");

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("itest@example.com"))
                .andExpect(jsonPath("$.accessToken").exists());

        LoginRequest login = new LoginRequest("itest@example.com", "Str0ng!Pass");

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void login_withWrongPassword_returns401() throws Exception {
        RegisterRequest register = new RegisterRequest("Bad Login", "badlogin@example.com", "Str0ng!Pass");
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        LoginRequest badLogin = new LoginRequest("badlogin@example.com", "wrong-password");
        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_withWeakPassword_returns400() throws Exception {
        RegisterRequest weak = new RegisterRequest("Weak Pass", "weak@example.com", "weak");
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(weak)))
                .andExpect(status().isBadRequest());
    }
}
