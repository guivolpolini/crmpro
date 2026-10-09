package com.crmpro.ai;

import com.crmpro.ai.dto.GeneratePitchRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AiCommercialIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve gerar pitch comercial via IA com autenticação e fallback integrado")
    void shouldGenerateCommercialPitch() throws Exception {
        // 1. Cadastrar organização e autenticar
        RegisterCompanyRequest reg = new RegisterCompanyRequest();
        reg.setCompanyName("AI Sales Pro Ltda");
        reg.setAdminName("Executivo Comercial");
        reg.setEmail("executivo.ai@salespro.com");
        reg.setPassword("SenhaForte@123");

        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/register-company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readTree(regResult.getResponse().getContentAsString())
                .path("data").path("accessToken").asText();

        // 2. Chamar endpoint de geração de pitch
        GeneratePitchRequest pitchReq = new GeneratePitchRequest();
        pitchReq.setRecipientName("Dr. Eduardo Santos");
        pitchReq.setSegment("Saúde / Clínicas Médicas");
        pitchReq.setPainPoints("Falta de acompanhamento de propostas enviadas aos convênios");
        pitchReq.setTargetProductName("CRM PRO Medical Suite");

        mockMvc.perform(post("/api/v1/ai/pitch")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pitchReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.subject").exists())
                .andExpect(jsonPath("$.data.pitchText").exists())
                .andExpect(jsonPath("$.data.callToAction").exists());
    }
}
