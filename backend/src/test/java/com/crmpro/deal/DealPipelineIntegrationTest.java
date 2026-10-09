package com.crmpro.deal;

import com.crmpro.auth.dto.LoginResponse;
import com.crmpro.auth.dto.RegisterCompanyRequest;
import com.crmpro.auth.service.AuthService;
import com.crmpro.deal.dto.DealRequest;
import com.crmpro.deal.dto.MoveStageRequest;
import com.crmpro.deal.entity.DealStatus;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DealPipelineIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("Quality Gate: Deve gerenciar o ciclo de vida de uma oportunidade e mover entre estágios do Kanban")
    void shouldCreateAndMoveDealAcrossPipelineStages() throws Exception {
        // 1. Cadastra organização
        RegisterCompanyRequest reg = new RegisterCompanyRequest();
        reg.setCompanyName("Logística Global");
        reg.setAdminName("Diretor Comercial");
        reg.setEmail("diretor@logisticaglobal.com");
        reg.setPassword("logistica123");
        LoginResponse loginResponse = authService.registerCompany(reg);
        String token = loginResponse.getAccessToken();

        // 2. Consulta estágios do funil (dispara auto-provisionamento de estágios)
        MvcResult stagesResult = mockMvc.perform(get("/api/v1/pipeline-stages")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", org.hamcrest.Matchers.hasSize(6)))
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
        assertThat(wonStageId).isNotBlank();

        // 3. Cria oportunidade no primeiro estágio
        DealRequest dealReq = new DealRequest();
        dealReq.setTitle("Contrato de Transporte Anual - Cliente Beta");
        dealReq.setStageId(UUID.fromString(firstStageId));
        dealReq.setAmount(new BigDecimal("75000.00"));
        dealReq.setProbability(30);

        MvcResult createDealResult = mockMvc.perform(post("/api/v1/deals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dealReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status", is("OPEN")))
                .andExpect(jsonPath("$.data.amount", is(75000.0)))
                .andReturn();

        String dealId = objectMapper.readTree(createDealResult.getResponse().getContentAsString())
                .path("data").path("id").asText();

        // 4. Move o Deal para o estágio Ganho
        MoveStageRequest moveReq = new MoveStageRequest();
        moveReq.setTargetStageId(UUID.fromString(wonStageId));

        mockMvc.perform(patch("/api/v1/deals/" + dealId + "/move")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moveReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("WON")))
                .andExpect(jsonPath("$.data.probability", is(100)))
                .andExpect(jsonPath("$.data.closedAt", org.hamcrest.Matchers.notNullValue()));
    }
}
