package com.crmpro.dashboard;

import com.crmpro.auth.dto.LoginResponse;
import com.crmpro.auth.dto.RegisterCompanyRequest;
import com.crmpro.auth.service.AuthService;
import com.crmpro.deal.dto.DealRequest;
import com.crmpro.deal.dto.MoveStageRequest;
import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.UUID;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardAnalyticsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("Quality Gate: Deve calcular indicadores e métricas financeiras do dashboard com precisão")
    void shouldCalculateDashboardMetricsAccurately() throws Exception {
        // 1. Cadastra organização
        RegisterCompanyRequest reg = new RegisterCompanyRequest();
        reg.setCompanyName("Dashboard Analytics Corp");
        reg.setAdminName("Executivo Chefe");
        reg.setEmail("executivo@dashboardanalytics.com");
        reg.setPassword("segredo123");
        LoginResponse loginResponse = authService.registerCompany(reg);
        String token = loginResponse.getAccessToken();

        // 2. Consulta estágios do pipeline
        MvcResult stagesResult = mockMvc.perform(get("/api/v1/pipeline-stages")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode stagesNode = objectMapper.readTree(stagesResult.getResponse().getContentAsString()).path("data");
        String firstStageId = stagesNode.get(0).path("id").asText();
        String wonStageId = "";
        for (JsonNode s : stagesNode) {
            if (s.path("won").asBoolean()) {
                wonStageId = s.path("id").asText();
                break;
            }
        }

        // 3. Cria oportunidade e fecha como ganha (R$ 80.000)
        DealRequest dealWon = new DealRequest();
        dealWon.setTitle("Contrato Enterprise Fechado");
        dealWon.setStageId(UUID.fromString(firstStageId));
        dealWon.setAmount(new BigDecimal("80000.00"));

        MvcResult wonResult = mockMvc.perform(post("/api/v1/deals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dealWon)))
                .andExpect(status().isCreated())
                .andReturn();

        String wonDealId = objectMapper.readTree(wonResult.getResponse().getContentAsString()).path("data").path("id").asText();

        MoveStageRequest moveWon = new MoveStageRequest();
        moveWon.setTargetStageId(UUID.fromString(wonStageId));
        mockMvc.perform(patch("/api/v1/deals/" + wonDealId + "/move")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moveWon)))
                .andExpect(status().isOk());

        // 4. Cria oportunidade em aberto (R$ 45.000)
        DealRequest dealOpen = new DealRequest();
        dealOpen.setTitle("Oportunidade em Negociação");
        dealOpen.setStageId(UUID.fromString(firstStageId));
        dealOpen.setAmount(new BigDecimal("45000.00"));

        mockMvc.perform(post("/api/v1/deals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dealOpen)))
                .andExpect(status().isCreated());

        // 5. Consulta métricas do Dashboard
        mockMvc.perform(get("/api/v1/dashboard/stats")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalRevenueWon", is(80000.0)))
                .andExpect(jsonPath("$.data.totalPipelineOpen", is(45000.0)))
                .andExpect(jsonPath("$.data.totalDealsCount", is(2)))
                .andExpect(jsonPath("$.data.stagesMetrics", org.hamcrest.Matchers.hasSize(greaterThanOrEqualTo(1))));
    }
}
