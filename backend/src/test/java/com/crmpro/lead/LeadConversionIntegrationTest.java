package com.crmpro.lead;

import com.crmpro.auth.dto.LoginResponse;
import com.crmpro.auth.dto.RegisterCompanyRequest;
import com.crmpro.auth.service.AuthService;
import com.crmpro.company.repository.CompanyRepository;
import com.crmpro.contact.repository.ContactRepository;
import com.crmpro.lead.dto.ConvertLeadRequest;
import com.crmpro.lead.dto.LeadRequest;
import com.crmpro.lead.entity.LeadStatus;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LeadConversionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Test
    @DisplayName("Quality Gate: Deve converter Lead em Contato e Empresa de forma transacional e idempotente")
    void shouldConvertLeadAtomically() throws Exception {
        // 1. Cadastra empresa para obter credenciais
        RegisterCompanyRequest reg = new RegisterCompanyRequest();
        reg.setCompanyName("Organização Vendas Top");
        reg.setAdminName("Vendedor Chefe");
        reg.setEmail("chefe@vendastop.com");
        reg.setPassword("senha123456");
        LoginResponse loginResponse = authService.registerCompany(reg);
        String token = loginResponse.getAccessToken();

        // 2. Cadastra Lead
        LeadRequest leadReq = new LeadRequest();
        leadReq.setName("Mariana Compradora");
        leadReq.setCompanyName("Indústria Alfa Ltda");
        leadReq.setEmail("mariana@indalfa.com");
        leadReq.setPhone("11987654321");
        leadReq.setSource("GOOGLE");
        leadReq.setStatus(LeadStatus.QUALIFIED);
        leadReq.setScore(85);

        MvcResult leadResult = mockMvc.perform(post("/api/v1/leads")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(leadReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andExpect(jsonPath("$.data.status", is("QUALIFIED")))
                .andReturn();

        String leadId = objectMapper.readTree(leadResult.getResponse().getContentAsString())
                .path("data").path("id").asText();

        // 3. Executa a conversão do Lead
        ConvertLeadRequest convertReq = new ConvertLeadRequest();
        convertReq.setCreateCompany(true);

        MvcResult convertResult = mockMvc.perform(post("/api/v1/leads/" + leadId + "/convert")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(convertReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.contactId", notNullValue()))
                .andExpect(jsonPath("$.data.companyId", notNullValue()))
                .andReturn();

        String contactId = objectMapper.readTree(convertResult.getResponse().getContentAsString())
                .path("data").path("contactId").asText();
        String companyId = objectMapper.readTree(convertResult.getResponse().getContentAsString())
                .path("data").path("companyId").asText();

        // 4. Valida persistência real no banco de dados
        assertThat(companyRepository.findById(java.util.UUID.fromString(companyId))).isPresent();
        assertThat(contactRepository.findById(java.util.UUID.fromString(contactId))).isPresent();

        // 5. Teste de Idempotência: Tentar converter novamente deve ser rejeitado
        mockMvc.perform(post("/api/v1/leads/" + leadId + "/convert")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(convertReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }
}
