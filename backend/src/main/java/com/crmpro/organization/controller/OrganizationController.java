package com.crmpro.organization.controller;

import com.crmpro.common.response.ApiResponse;
import com.crmpro.organization.dto.OrganizationResponse;
import com.crmpro.organization.dto.UpdateOrganizationRequest;
import com.crmpro.organization.service.OrganizationManagementService;
import com.crmpro.user.dto.CreateUserRequest;
import com.crmpro.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organization")
@RequiredArgsConstructor
@Tag(name = "Gestão da Organização & Equipe", description = "Endpoints para configuração de perfil corporativo, plano e gestão de membros da equipe")
public class OrganizationController {

    private final OrganizationManagementService orgService;

    @GetMapping
    @Operation(summary = "Obter dados da organização do tenant atual")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getCurrentOrganization() {
        OrganizationResponse org = orgService.getCurrentOrganization();
        return ResponseEntity.ok(ApiResponse.ok(org));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar informações da organização (Apenas Administradores)")
    public ResponseEntity<ApiResponse<OrganizationResponse>> updateOrganization(
            @Valid @RequestBody UpdateOrganizationRequest request
    ) {
        OrganizationResponse org = orgService.updateOrganization(request);
        return ResponseEntity.ok(ApiResponse.ok("Dados da organização atualizados com sucesso", org));
    }

    @GetMapping("/users")
    @Operation(summary = "Listar membros da equipe da organização")
    public ResponseEntity<ApiResponse<List<UserResponse>>> listTeamMembers() {
        List<UserResponse> users = orgService.listTeamMembers();
        return ResponseEntity.ok(ApiResponse.ok(users));
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar novo colaborador na organização (Apenas Administradores)")
    public ResponseEntity<ApiResponse<UserResponse>> addTeamMember(
            @Valid @RequestBody CreateUserRequest request
    ) {
        UserResponse user = orgService.addTeamMember(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Colaborador adicionado com sucesso", user));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remover colaborador da organização (Apenas Administradores)")
    public ResponseEntity<ApiResponse<Void>> removeTeamMember(@PathVariable UUID id) {
        orgService.removeTeamMember(id);
        return ResponseEntity.ok(ApiResponse.ok("Colaborador removido com sucesso", null));
    }
}
