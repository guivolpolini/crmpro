package com.crmpro.auth;

import com.crmpro.auth.dto.LoginRequest;
import com.crmpro.auth.dto.RegisterCompanyRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve registrar empresa e usuário administrador com sucesso e emitir JWT")
    void shouldRegisterCompanySuccessfully() throws Exception {
        RegisterCompanyRequest request = new RegisterCompanyRequest();
        request.setCompanyName("Acme Corp");
        request.setDocument("12.345.678/0001-90");
        request.setPhone("11999998888");
        request.setAdminName("Admin Acme");
        request.setEmail("admin@acme.com");
        request.setPassword("senha123");

        mockMvc.perform(post("/api/v1/auth/register-company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.accessToken", notNullValue()))
                .andExpect(jsonPath("$.data.organization.name", is("Acme Corp")))
                .andExpect(jsonPath("$.data.user.email", is("admin@acme.com")))
                .andExpect(jsonPath("$.data.user.role", is("ADMIN")));
    }

    @Test
    @DisplayName("Deve autenticar com sucesso usuário existente e permitir acesso ao endpoint /me")
    void shouldLoginAndAccessProtectedEndpoint() throws Exception {
        // 1. Cadastra empresa
        RegisterCompanyRequest registerReq = new RegisterCompanyRequest();
        registerReq.setCompanyName("Tech Solutions");
        registerReq.setAdminName("Carlos Tech");
        registerReq.setEmail("carlos@techsolutions.com");
        registerReq.setPassword("tech12345");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/auth/register-company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andReturn();

        // 2. Realiza login
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("carlos@techsolutions.com");
        loginReq.setPassword("tech12345");

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken", notNullValue()))
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        String accessToken = objectMapper.readTree(responseBody).path("data").path("accessToken").asText();

        // 3. Acessa endpoint protegido /me
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email", is("carlos@techsolutions.com")))
                .andExpect(jsonPath("$.data.name", is("Carlos Tech")));
    }

    @Test
    @DisplayName("Deve rejeitar login com senha incorreta retornando 401 Unauthorized")
    void shouldRejectInvalidCredentials() throws Exception {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("carlos@techsolutions.com");
        loginReq.setPassword("senha_errada");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)));
    }
}
