package com.crmpro.task;

import com.crmpro.auth.dto.LoginResponse;
import com.crmpro.auth.dto.RegisterCompanyRequest;
import com.crmpro.auth.service.AuthService;
import com.crmpro.deal.dto.DealRequest;
import com.crmpro.proposal.dto.ProductRequest;
import com.crmpro.proposal.dto.ProposalRequest;
import com.crmpro.task.dto.TaskRequest;
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
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskAndProposalIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("Quality Gate: Deve criar tarefas com toggle de conclusão e emitir propostas com catálogo de produtos")
    void shouldManageTasksAndProposals() throws Exception {
        // 1. Cadastra empresa
        RegisterCompanyRequest reg = new RegisterCompanyRequest();
        reg.setCompanyName("Agência Crescimento");
        reg.setAdminName("Camila Gestora");
        reg.setEmail("camila@crescimento.com");
        reg.setPassword("segredo123");
        LoginResponse loginResponse = authService.registerCompany(reg);
        String token = loginResponse.getAccessToken();

        // 2. Consulta estágios com token autenticado para ter TenantContext ativo
        MvcResult stagesResult = mockMvc.perform(get("/api/v1/pipeline-stages")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String firstStageId = objectMapper.readTree(stagesResult.getResponse().getContentAsString())
                .path("data").get(0).path("id").asText();

        // 3. Cria Deal
        DealRequest dealReq = new DealRequest();
        dealReq.setTitle("Projeto Transformação Digital");
        dealReq.setStageId(UUID.fromString(firstStageId));
        dealReq.setAmount(new BigDecimal("120000.00"));

        MvcResult dealResult = mockMvc.perform(post("/api/v1/deals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dealReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String dealId = objectMapper.readTree(dealResult.getResponse().getContentAsString())
                .path("data").path("id").asText();

        // 4. Cria Tarefa vinculada ao Deal
        TaskRequest taskReq = new TaskRequest();
        taskReq.setTitle("Enviar briefing técnico para diretoria");
        taskReq.setDealId(UUID.fromString(dealId));
        taskReq.setPriority("HIGH");
        taskReq.setDueDate(Instant.now().plusSeconds(86400));

        MvcResult taskResult = mockMvc.perform(post("/api/v1/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status", is("PENDING")))
                .andReturn();

        String taskId = objectMapper.readTree(taskResult.getResponse().getContentAsString())
                .path("data").path("id").asText();

        // 5. Alterna conclusão da tarefa (Toggle)
        mockMvc.perform(patch("/api/v1/tasks/" + taskId + "/toggle")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("COMPLETED")))
                .andExpect(jsonPath("$.data.completedAt", notNullValue()));

        // 6. Cadastra Produto no catálogo
        ProductRequest productReq = new ProductRequest();
        productReq.setName("Licença Anual CRM Enterprise");
        productReq.setCode("LIC-ENT-01");
        productReq.setUnitPrice(new BigDecimal("60000.00"));

        mockMvc.perform(post("/api/v1/proposals/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name", is("Licença Anual CRM Enterprise")));

        // 7. Emite Proposta Comercial
        ProposalRequest propReq = new ProposalRequest();
        propReq.setDealId(UUID.fromString(dealId));
        propReq.setTotalAmount(new BigDecimal("120000.00"));
        propReq.setDiscount(new BigDecimal("5000.00"));
        propReq.setValidUntil(LocalDate.now().plusDays(30));
        propReq.setNotes("Condição de pagamento em 12x sem juros");

        mockMvc.perform(post("/api/v1/proposals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(propReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.code", notNullValue()))
                .andExpect(jsonPath("$.data.totalAmount", is(120000.0)))
                .andExpect(jsonPath("$.data.status", is("DRAFT")));
    }
}
