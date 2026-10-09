package com.crmpro.security;

import com.crmpro.auth.dto.LoginResponse;
import com.crmpro.auth.dto.RegisterCompanyRequest;
import com.crmpro.auth.service.AuthService;
import com.crmpro.user.entity.User;
import com.crmpro.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MultiTenancySecurityTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Quality Gate: Deve isolar estritamente usuários de organizações distintas impedindo vazamento de dados")
    void shouldStrictlyIsolateDataBetweenOrganizations() {
        // 1. Cadastra Organização Alpha
        RegisterCompanyRequest orgAlpha = new RegisterCompanyRequest();
        orgAlpha.setCompanyName("Alpha Corp");
        orgAlpha.setAdminName("Admin Alpha");
        orgAlpha.setEmail("admin@alpha.corp");
        orgAlpha.setPassword("alphaSecret123");
        LoginResponse alphaResponse = authService.registerCompany(orgAlpha);

        // 2. Cadastra Organização Beta
        RegisterCompanyRequest orgBeta = new RegisterCompanyRequest();
        orgBeta.setCompanyName("Beta Industries");
        orgBeta.setAdminName("Admin Beta");
        orgBeta.setEmail("admin@beta.ind");
        orgBeta.setPassword("betaSecret123");
        LoginResponse betaResponse = authService.registerCompany(orgBeta);

        // 3. Valida isolamento em nível de repositório e chaves estrangeiras
        assertThat(alphaResponse.getOrganization().getId())
                .isNotEqualTo(betaResponse.getOrganization().getId());

        // Busca por organizationId de Alpha não pode retornar usuários de Beta
        List<User> alphaUsers = userRepository.findAllByOrganizationId(alphaResponse.getOrganization().getId());
        assertThat(alphaUsers).hasSize(1);
        assertThat(alphaUsers.get(0).getEmail()).isEqualTo("admin@alpha.corp");

        List<User> betaUsers = userRepository.findAllByOrganizationId(betaResponse.getOrganization().getId());
        assertThat(betaUsers).hasSize(1);
        assertThat(betaUsers.get(0).getEmail()).isEqualTo("admin@beta.ind");

        // Busca de usuário de Beta filtrando pela organização de Alpha deve retornar vazio
        Optional<User> crossTenantUser = userRepository.findByIdAndOrganizationId(
                betaResponse.getUser().getId(),
                alphaResponse.getOrganization().getId()
        );
        assertThat(crossTenantUser).isEmpty();
    }
}
