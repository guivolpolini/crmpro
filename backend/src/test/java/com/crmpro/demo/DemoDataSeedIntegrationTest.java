package com.crmpro.demo;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DemoDataSeedIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve popular organização com dados ricos de demonstração e refletir nas métricas")
    void shouldSeedDemoDataAndReflectInMetrics() throws Exception {
        // 1. Cadastrar organização com Admin
        RegisterCompanyRequest reg = new RegisterCompanyRequest();
        reg.setCompanyName("Demo Showcase Corp");
        reg.setAdminName("Admin Showcase");
        reg.setEmail("admin.showcase@demoshowcase.com");
        reg.setPassword("SenhaForte@123");

        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/register-company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readTree(regResult.getResponse().getContentAsString())
                .path("data").path("accessToken").asText();

        // 2. Chamar seed de dados de demonstração
        mockMvc.perform(post("/api/v1/demo/seed")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 3. Validar se o Dashboard reflete os dados gerados
        mockMvc.perform(get("/api/v1/dashboard/stats")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalRevenueWon").value(120000.00))
                .andExpect(jsonPath("$.data.totalCompaniesCount").value(3))
                .andExpect(jsonPath("$.data.totalContactsCount").value(3))
                .andExpect(jsonPath("$.data.totalLeadsCount").value(3));

        // 4. Validar se os deals estão disponíveis para o Kanban
        mockMvc.perform(get("/api/v1/deals/kanban")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(4));
    }
}
