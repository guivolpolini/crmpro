package com.crmpro.organization;

import com.crmpro.auth.dto.RegisterCompanyRequest;
import com.crmpro.organization.dto.UpdateOrganizationRequest;
import com.crmpro.user.dto.CreateUserRequest;
import com.crmpro.user.entity.Role;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrganizationManagementIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve consultar, atualizar organização e gerenciar membros de equipe com sucesso")
    void shouldManageOrganizationAndTeamMembers() throws Exception {
        // 1. Cadastrar organização com Admin
        RegisterCompanyRequest reg = new RegisterCompanyRequest();
        reg.setCompanyName("Alpha Holding Inc");
        reg.setAdminName("Admin Alpha");
        reg.setEmail("admin.alpha@alphaholding.com");
        reg.setPassword("SenhaForte@123");

        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/register-company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readTree(regResult.getResponse().getContentAsString())
                .path("data").path("accessToken").asText();

        // 2. Consultar organização atual
        mockMvc.perform(get("/api/v1/organization")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Alpha Holding Inc"));

        // 3. Atualizar dados corporativos
        UpdateOrganizationRequest updateReq = new UpdateOrganizationRequest();
        updateReq.setName("Alpha Global Solutions");
        updateReq.setLegalName("Alpha Global Solutions Ltda");
        updateReq.setDocument("12.345.678/0001-90");
        updateReq.setPhone("(11) 98765-4321");
        updateReq.setEmail("contato@alphaglobal.com");

        mockMvc.perform(put("/api/v1/organization")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Alpha Global Solutions"));

        // 4. Convidar novo vendedor para a equipe
        CreateUserRequest userReq = new CreateUserRequest();
        userReq.setName("Beatriz Vendas");
        userReq.setEmail("beatriz.seller@alphaglobal.com");
        userReq.setPassword("Senha123@");
        userReq.setRole(Role.SELLER);

        MvcResult userResult = mockMvc.perform(post("/api/v1/organization/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value("beatriz.seller@alphaglobal.com"))
                .andExpect(jsonPath("$.data.role").value("SELLER"))
                .andReturn();

        // 5. Listar equipe
        mockMvc.perform(get("/api/v1/organization/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }
}
