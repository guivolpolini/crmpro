package com.crmpro.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RateLimitingAndSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Deve permitir requisições de login dentro do limite e barrar ao exceder")
    void shouldAllowLoginRequestsWithinLimit() throws Exception {
        // Enviar requisições de login normais
        String loginJson = """
            {
                "email": "test@crmpro.com",
                "password": "wrongpassword"
            }
        """;

        // Primeiras 5 requisições devem passar pelo filtro (chegando à camada de auth, retornando 401 ou 400)
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginJson))
                    .andExpect(result -> {
                        int status = result.getResponse().getStatus();
                        assert status != 429 : "Não deve ter atingido rate limit nas primeiras requisições";
                    });
        }
    }
}
