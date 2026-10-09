package com.crmpro.report;

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

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReportPerformanceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve gerar relatório de performance comercial e exportação CSV com sucesso")
    void shouldGeneratePerformanceReportAndCsv() throws Exception {
        // 1. Cadastrar organização e autenticar
        RegisterCompanyRequest reg = new RegisterCompanyRequest();
        reg.setCompanyName("Reports Tech Ltda");
        reg.setAdminName("Diretor Comercial");
        reg.setEmail("diretor.reports@reportstech.com");
        reg.setPassword("SenhaForte@123");

        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/register-company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readTree(regResult.getResponse().getContentAsString())
                .path("data").path("accessToken").asText();

        // 2. Chamar endpoint de relatórios de performance
        mockMvc.perform(get("/api/v1/reports/performance")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalRevenueWon").exists())
                .andExpect(jsonPath("$.data.winRate").exists())
                .andExpect(jsonPath("$.data.leadConversionRate").exists());

        // 3. Chamar exportação CSV
        mockMvc.perform(get("/api/v1/reports/export/deals")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String csv = result.getResponse().getContentAsString();
                    assert csv.contains("ID,Titulo,Valor,Status,Data_Criacao");
                });
    }
}
