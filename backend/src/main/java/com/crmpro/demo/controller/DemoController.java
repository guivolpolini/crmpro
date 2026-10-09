package com.crmpro.demo.controller;

import com.crmpro.common.response.ApiResponse;
import com.crmpro.demo.service.DemoDataSeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo")
@RequiredArgsConstructor
@Tag(name = "Demonstração & Carga de Dados", description = "Endpoints para população de dados comerciais realistas de demonstração")
public class DemoController {

    private final DemoDataSeedService demoDataSeedService;

    @PostMapping("/seed")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Popular organização atual com dados de demonstração (Empresas, Contatos, Leads, Deals no Kanban, Tarefas e Propostas)")
    public ResponseEntity<ApiResponse<Void>> seedDemoData() {
        demoDataSeedService.seedDemoDataForCurrentTenant();
        return ResponseEntity.ok(ApiResponse.ok("Dados de demonstração gerados com sucesso para a organização", null));
    }
}
