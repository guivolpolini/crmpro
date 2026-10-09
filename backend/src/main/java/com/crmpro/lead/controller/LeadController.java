package com.crmpro.lead.controller;

import com.crmpro.common.response.ApiResponse;
import com.crmpro.lead.dto.*;
import com.crmpro.lead.entity.LeadStatus;
import com.crmpro.lead.service.LeadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leads")
@RequiredArgsConstructor
@Tag(name = "Leads", description = "Endpoints para gestão e conversão de leads comerciais")
public class LeadController {

    private final LeadService leadService;

    @GetMapping
    @Operation(summary = "Listar leads com paginação, busca e filtro de status")
    public ResponseEntity<ApiResponse<Page<LeadResponse>>> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) LeadStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        Page<LeadResponse> page = leadService.findAll(search, status, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar lead por ID")
    public ResponseEntity<ApiResponse<LeadResponse>> findById(@PathVariable UUID id) {
        LeadResponse response = leadService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo lead")
    public ResponseEntity<ApiResponse<LeadResponse>> create(@Valid @RequestBody LeadRequest request) {
        LeadResponse response = leadService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Lead cadastrado com sucesso", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar lead existente")
    public ResponseEntity<ApiResponse<LeadResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody LeadRequest request
    ) {
        LeadResponse response = leadService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Lead atualizado com sucesso", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover lead")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        leadService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Lead removido com sucesso", null));
    }

    @PostMapping("/{id}/convert")
    @Operation(summary = "Converter lead em Contato e Empresa de forma transacional")
    public ResponseEntity<ApiResponse<ConvertLeadResponse>> convertLead(
            @PathVariable UUID id,
            @RequestBody(required = false) ConvertLeadRequest request
    ) {
        if (request == null) {
            request = new ConvertLeadRequest();
        }
        ConvertLeadResponse response = leadService.convertLead(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Lead convertido com sucesso", response));
    }
}
